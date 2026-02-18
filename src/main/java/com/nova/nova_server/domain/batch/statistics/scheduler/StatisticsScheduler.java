package com.nova.nova_server.domain.batch.statistics.scheduler;

import com.nova.nova_server.domain.batch.common.service.BatchJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsScheduler {

    private final BatchJobService batchJobService;

    @Scheduled(cron = "${batch.statistics.cron}")
    public void runStatisticsBatch() {
        log.info("Daily statistics batch triggered by scheduler");
        try {
            batchJobService.runStatisticsBatch();
        } catch (Exception e) {
            log.error("Scheduled statistics batch execution failed", e);
        }
    }
}
