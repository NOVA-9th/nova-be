package com.nova.nova_server.domain.batch.service;

import com.nova.nova_server.domain.ai.service.AiBatchService;
import com.nova.nova_server.domain.batch.converter.ArticleToPromptConverter;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    /**
     * 메인 배치 프로세스를 실행합니다.
     */
    @Async
    public void executeBatch() {
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
            // 30초 간격이므로 2번에 한 번(1분) 로그 출력
            if (i % 2 == 0) {
                log.info("Waiting for OpenAI batch completion... (attempt {}/{}): batchId={}", i + 1, MAX_POLLING_COUNT,
                        batchId);
            }

            if (aiBatchService.isCompleted(batchId)) {
                log.info("OpenAI batch completed successfully! batchId={}", batchId);
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
}
