package com.nova.nova_server.domain.batch.scheduler;

import com.nova.nova_server.domain.batch.service.CardNewsBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 카드 뉴스 배치를 주기적으로 관리하는 스케줄러입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CardNewsBatchScheduler {

    private final CardNewsBatchService cardNewsBatchService;

    /**
     * 매일 정해진 시간(새벽 5시)에 배치를 실행합니다.
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
