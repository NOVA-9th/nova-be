package com.nova.nova_server.domain.batch.articleingestion.scheduler;

import com.nova.nova_server.domain.batch.articleingestion.service.ArticleIngestionJobRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleIngestionScheduler {

    private final ArticleIngestionJobRunner articleIngestionBatchService;

    /**
     * application.yml 에 정의된 cron 시간에 배치를 실행합니다.
     */
    @Scheduled(cron = "${batch.article-ingestion.cron}")
    public void runDailyBatch() {
        log.info("Daily CardNews batch triggered by scheduler");
        try {
            articleIngestionBatchService.runArticleIngestionAndSummaryBatch();
        } catch (Exception e) {
            log.error("Scheduled batch execution failed", e);
        }
    }
}
