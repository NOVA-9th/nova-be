package com.nova.nova_server.domain.batch.summary.service;

import com.nova.nova_server.domain.ai.service.AiBatchService;
import com.nova.nova_server.domain.batch.summary.converter.ArticleConverter;
import com.nova.nova_server.domain.batch.summary.dto.LlmSummaryResult;
import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import com.nova.nova_server.domain.batch.common.entity.ArticleState;
import com.nova.nova_server.domain.batch.common.repository.ArticleEntityRepository;
import com.nova.nova_server.domain.batch.common.service.CardNewsSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleSummaryWriter implements ItemWriter<ArticleEntity> {

    private static final int POLLING_INTERVAL_MS = 10_000;
    // 타임아웃 주의: 실제 운영 시에는 트랜잭션 타임아웃을 고려해 값을 조정하거나 비동기 구조로 변경 고려
    private static final int MAX_POLLING_COUNT = 120; // 예: 1시간 (30초 * 120회)

    private final ArticleConverter articleToPromptConverter;
    private final AiBatchService aiBatchService;
    private final ArticleEntityRepository articleEntityRepository;
    private final CardNewsSaveService cardNewsSaveService;

    @Override
    public void write(Chunk<? extends ArticleEntity> chunk) {
        if (chunk.isEmpty()) {
            return;
        }

        log.info("ArticleSummaryWriter: Processing chunk of {} items", chunk.size());

        Map<String, String> prompts = articleToPromptConverter.chunkToPromptMap(chunk);
        String batchId = aiBatchService.createBatch(prompts);

        log.info("Batch submitted. BatchId: {}, Count: {}", batchId, chunk.size());

        boolean isSuccess = waitForCompletion(batchId);

        if (isSuccess) {
            Map<String, String> batchResult = aiBatchService.getResults(batchId);
            Map<ArticleEntity, LlmSummaryResult> summaryResult = articleToPromptConverter.fromBatchResult(chunk, batchResult);

            int completedCount = 0, failedCount = 0;
            for (Map.Entry<ArticleEntity, LlmSummaryResult> entry : summaryResult.entrySet()) {
                ArticleEntity articleEntity = entry.getKey();
                LlmSummaryResult llmSummaryResult = entry.getValue();

                if (llmSummaryResult != null) {
                    cardNewsSaveService.saveSingleCardNews(articleEntity, llmSummaryResult);
                    articleEntity.setState(ArticleState.COMPLETED);
                    articleEntityRepository.save(articleEntity);
                    completedCount++;
                }
                else {
                    articleEntity.setState(ArticleState.FAILED);
                    articleEntityRepository.save(articleEntity);
                    failedCount++;
                }
            }

            log.info("CardNews saved COMPLETED={}, FAILED={}, TOTAL={}", completedCount, failedCount, completedCount);
            log.info("Chunk processing completed successfully for BatchId: {}", batchId);
        } else {
            // 실패 시 로직 (상태를 FAILED로 변경 등)
            log.error("OpenAI Batch failed or timed out. BatchId: {}", batchId);
            updateEntityState(chunk, ArticleState.FAILED);
            throw new RuntimeException("AI Batch processing failed for batchId: " + batchId);
        }
    }

    private void updateEntityState(Chunk<? extends ArticleEntity> entities, ArticleState state) {
        for (ArticleEntity entity : entities) {
            entity.setState(state);
        }
        articleEntityRepository.saveAll(entities);
    }

    private boolean waitForCompletion(String batchId) {
        for (int i = 0; i < MAX_POLLING_COUNT; i++) {
            log.info("Waiting for AI Batch... ({}/{}): batchId={}", i + 1, MAX_POLLING_COUNT, batchId);

            // AI 서비스에 완료 여부 체크
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
        return false; // 시간 초과
    }
}