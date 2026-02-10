package com.nova.nova_server.domain.batch.articleingestion.scheduler;

import com.nova.nova_server.domain.batch.common.service.BatchJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleIngestionScheduler {

    private final BatchJobService batchJobService;

    /**
     * 매일 정해진 시간(새벽 5시)에 배치를 실행합니다.
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void runDailyBatch() {
        log.info("Daily CardNews batch triggered by scheduler");
        try {
            batchJobService.runArticleIngestionBatch();
        } catch (Exception e) {
            log.error("Scheduled batch execution failed", e);
        }
    }
}
