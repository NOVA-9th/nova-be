package com.nova.nova_server.domain.batch.service;

import com.nova.nova_server.domain.ai.service.AiBatchService;
import com.nova.nova_server.domain.batch.converter.ArticleToPromptConverter;
import com.nova.nova_server.domain.batch.entity.BatchRunMetadata;
import com.nova.nova_server.domain.batch.repository.BatchRunMetadataRepository;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 카드 뉴스 생성 배치의 전체 흐름을 관리하는 클래스입니다.
 * 수집 -> AI 요약 요청 -> 완료 대기(Polling) -> 결과 저장 순서로 진행됩니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardNewsBatchService {

    private static final String JOB_NAME = "card-news-batch";
    private static final int POLLING_INTERVAL_MS = 30_000; // 30초
    private static final int MAX_POLLING_COUNT = 2880; // 최대 24시간 (30초 * 2880)

    private final ArticleFetchService articleFetchService;
    private final ArticleToPromptConverter articleToPromptConverter;
    private final AiBatchService aiBatchService;
    private final CardNewsSaveService cardNewsSaveService;
    private final BatchRunMetadataRepository batchRunMetadataRepository;

    /**
     * 메인 배치 프로세스를 실행합니다.
     */
    @Async
    @Transactional
    public void executeBatch() {
        if (batchRunMetadataRepository.existsByJobNameAndStatus(JOB_NAME, "RUNNING")) {
            log.warn("CardNews batch is already running. Skipping this execution.");
            return;
        }

        LocalDateTime startTime = LocalDateTime.now();
        log.info("CardNews batch started at {}", startTime);

        BatchRunMetadata metadata = BatchRunMetadata.builder()
                .jobName(JOB_NAME)
                .executedAt(startTime)
                .status("RUNNING")
                .build();
        batchRunMetadataRepository.save(metadata);

        try {
            // 1. 아티클 수집
            List<Article> articles = articleFetchService.fetchAllArticles();
            if (articles.isEmpty()) {
                log.info("No new articles to process");
                updateMetadataStatus(metadata, "COMPLETED");
                return;
            }

            // 2. 배치 생성 (AI 요청)
            String batchId = createBatchFromArticles(articles);

            // 3. 폴링 (완료 대기) - 이 과정은 별도 스레드에서 블로킹됨
            boolean completed = waitForCompletion(batchId);
            if (!completed) {
                log.error("Batch did not complete in time: batchId={}", batchId);
                updateMetadataStatus(metadata, "TIMEOUT");
                return;
            }

            // 4. 결과 조회 및 저장
            Map<String, String> results = aiBatchService.getResults(batchId);
            int savedCount = cardNewsSaveService.saveCardNews(articles, results);

            log.info("CardNews batch completed: saved={}/{}", savedCount, articles.size());
            updateMetadataStatus(metadata, "COMPLETED");

        } catch (Exception e) {
            log.error("CardNews batch failed", e);
            updateMetadataStatus(metadata, "FAILED");
            // 비동기 작업이므로 호출자에게 예외를 던지기보다 로그만 남김
        }
    }

    /**
     * Article 목록을 LLM 프롬프트로 변환 후 배치 생성
     */
    public String createBatchFromArticles(List<Article> articles) {
        if (articles == null || articles.isEmpty()) {
            throw new IllegalArgumentException("articles cannot be empty");
        }

        Map<String, String> prompts = articleToPromptConverter.toPromptMap(articles);
        String batchId = aiBatchService.createBatch(prompts);
        log.info("Batch created from {} articles: batchId={}", articles.size(), batchId);
        return batchId;
    }

    private boolean waitForCompletion(String batchId) {
        for (int i = 0; i < MAX_POLLING_COUNT; i++) {
            if (aiBatchService.isCompleted(batchId)) {
                return true;
            }
            try {
                Thread.sleep(POLLING_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    private void updateMetadataStatus(BatchRunMetadata metadata, String status) {
        metadata.updateStatus(status);
        batchRunMetadataRepository.save(metadata);
    }
}
