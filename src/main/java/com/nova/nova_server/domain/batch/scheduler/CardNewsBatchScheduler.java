package com.nova.nova_server.domain.batch.scheduler;

import com.nova.nova_server.domain.batch.service.CardNewsBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 카드뉴스 배치 스케줄러
 * 매일 새벽 5시에 배치 작업 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CardNewsBatchScheduler {

    private final CardNewsBatchService cardNewsBatchService;

    /**
     * 매일 새벽 5시 배치 실행
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void runDailyBatch() {
        log.info("Daily CardNews batch triggered by scheduler");
        try {
            cardNewsBatchService.executeBatch();
        } catch (Exception e) {
            log.error("Scheduled batch execution failed", e);
        }
    }
}
