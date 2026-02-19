package com.nova.nova_server.domain.batch.statistics.scheduler;

import com.nova.nova_server.domain.batch.statistics.service.StatisticsJobRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsScheduler {

    private final StatisticsJobRunner statisticsBatchService;

    @Scheduled(cron = "${batch.statistics.cron}")
    public void runStatisticsBatch() {
        log.info("Daily statistics batch triggered by scheduler");
        try {
            LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
            LocalDateTime from = utcNow.minusDays(1);
            statisticsBatchService.runStatisticsBatch(from, utcNow);
        } catch (Exception e) {
            log.error("Scheduled statistics batch execution failed", e);
        }
    }
}
