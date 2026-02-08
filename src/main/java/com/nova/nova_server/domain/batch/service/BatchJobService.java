package com.nova.nova_server.domain.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Launches the Spring Batch article ingestion job.
 * Readers: ArticleApiService (ItemReader). Processor: ArticleSource → Article. Writer: Article → DB.
 */
@Slf4j
@Service
public class BatchJobService {

    private static final String JOB_NAME = "articleIngestionJob";

    private final JobLauncher jobLauncher;
    private final Job articleIngestionJob;

    public BatchJobService(JobLauncher jobLauncher,
                           @Qualifier("articleIngestionJob") Job articleIngestionJob) {
        this.jobLauncher = jobLauncher;
        this.articleIngestionJob = articleIngestionJob;
    }

    /**
     * Runs the Spring Batch article ingestion job (read from all providers → process → write to DB).
     */
    public void runArticleIngestionBatch() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("runAt", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(articleIngestionJob, params);
            log.info("Spring Batch job {} launched", JOB_NAME);
        } catch (Exception e) {
            log.error("Failed to launch job {}", JOB_NAME, e);
            throw new RuntimeException("Batch job launch failed", e);
        }
    }
}
