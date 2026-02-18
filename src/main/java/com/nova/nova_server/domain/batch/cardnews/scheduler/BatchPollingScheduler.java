package com.nova.nova_server.domain.batch.cardnews.scheduler;

import com.nova.nova_server.domain.batch.common.entity.AiBatchEntity;
import com.nova.nova_server.domain.batch.common.entity.AiBatchState;
import com.nova.nova_server.domain.batch.common.repository.AiBatchRepository;
import com.nova.nova_server.domain.batch.common.service.BatchJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchPollingScheduler {
    private final AiBatchRepository aiBatchRepository;
    private final BatchJobService batchJobService;

    @Scheduled(fixedDelay = 1000 * 10)
    public void pollBatch() {
        List<AiBatchEntity> batchEntities = aiBatchRepository.findAllByState(AiBatchState.PROGRESS);
        if (batchEntities.isEmpty()) {
            return;
        }

        log.info("진행중인 배치 작업 {} 개 발견", batchEntities.size());
        for (AiBatchEntity entity : batchEntities) {
            try {
                batchJobService.runCardNewsBatchProcessingJob(entity.getBatchId());
            } catch (Exception e) {
                log.error("Card news batch processing job execution failed for batchId={}", entity.getBatchId(), e);
            }
        }
    }
}
