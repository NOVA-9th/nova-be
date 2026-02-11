package com.nova.nova_server.domain.batch.cardnews.scheduler;

import com.nova.nova_server.domain.batch.cardnews.service.BatchProcessingService;
import com.nova.nova_server.domain.batch.common.entity.AiBatchEntity;
import com.nova.nova_server.domain.batch.common.entity.AiBatchState;
import com.nova.nova_server.domain.batch.common.repository.AiBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchPollingScheduler {
    private final BatchProcessingService batchProcessingService;
    private final AiBatchRepository aiBatchRepository;

    @Scheduled(fixedDelay = 1000 * 10)
    public void pollBatch() {
        List<AiBatchEntity> batchEntities = aiBatchRepository.findAllByState(AiBatchState.PROGRESS);
        log.info("진행중인 배치 작업 {} 개 발견", batchEntities.size());

        for (AiBatchEntity entity : batchEntities) {
            batchProcessingService.processBatchResult(entity);
        }
    }
}
