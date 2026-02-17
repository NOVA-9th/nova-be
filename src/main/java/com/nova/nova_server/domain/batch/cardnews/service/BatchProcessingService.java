package com.nova.nova_server.domain.batch.cardnews.service;

import com.nova.nova_server.domain.ai.exception.AiException;
import com.nova.nova_server.domain.ai.service.AiBatchService;
import com.nova.nova_server.domain.batch.common.entity.AiBatchEntity;
import com.nova.nova_server.domain.batch.common.entity.AiBatchState;
import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import com.nova.nova_server.domain.batch.common.entity.ArticleState;
import com.nova.nova_server.domain.batch.common.repository.AiBatchRepository;
import com.nova.nova_server.domain.batch.common.repository.ArticleEntityRepository;
import com.nova.nova_server.domain.batch.summary.dto.LlmSummaryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchProcessingService {
    private final AiBatchService aiBatchService;
    private final ArticleEntityRepository articleEntityRepository;
    private final CardNewsSaveService cardNewsSaveService;
    private final AiBatchRepository aiBatchRepository;

    @Transactional
    public void processBatchResult(AiBatchEntity entity) {
        try {
            if (aiBatchService.isCompleted(entity.getBatchId())) {
                log.info("배치 {} 성공", entity.getBatchId());
                onBatchSuccess(entity.getBatchId());
                entity.setState(AiBatchState.COMPLETED);
            }
        }
        catch (AiException e) {
            log.error("배치 {} 실패", entity.getBatchId(), e);
            onBatchFailed(entity.getBatchId());
            entity.setState(AiBatchState.FAILED);
        }

        aiBatchRepository.save(entity);
    }

    private void onBatchSuccess(String batchId) {
        Map<Long, LlmSummaryResult> summaryResult = aiBatchService.getResults(batchId, LlmSummaryResult.class)
                .entrySet().stream().collect(Collectors.toMap(
                        entry -> Long.parseLong(entry.getKey()),
                        Map.Entry::getValue
                ));
        Map<Long, ArticleEntity> entities = articleEntityRepository.findAllByIdIn(summaryResult.keySet())
                .stream()
                .collect(Collectors.toMap(
                        ArticleEntity::getId,
                        entity -> entity
                ));

        int completedCount = 0, failedCount = 0;
        for (Map.Entry<Long, LlmSummaryResult> entry : summaryResult.entrySet()) {
            Long articleId = entry.getKey();
            ArticleEntity entity = entities.getOrDefault(articleId, null);
            if (entity == null) {
                log.warn("존재하지 않는 ArticleEntity 에 대한 LlmSummaryResult 발견: {}", articleId);
                continue;
            }

            LlmSummaryResult llmSummaryResult = entry.getValue();
            if (llmSummaryResult != null) {
                cardNewsSaveService.saveSingleCardNews(entity, llmSummaryResult);
                entity.setState(ArticleState.COMPLETED);
                completedCount++;
            }
            else {
                entity.setState(ArticleState.FAILED);
                failedCount++;
            }
        }

        log.info("CardNews saved COMPLETED={}, FAILED={}, TOTAL={}", completedCount, failedCount, completedCount);
        log.info("Chunk processing completed successfully for BatchId: {}", batchId);
    }

    private void onBatchFailed(String batchId) {
        int updatedCount = articleEntityRepository.updateStateByBatchId(batchId, ArticleState.FAILED);
        log.info("Batch {} marked {} article(s) as FAILED", batchId, updatedCount);
    }
}
