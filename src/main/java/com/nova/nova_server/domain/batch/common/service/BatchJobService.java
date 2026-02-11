package com.nova.nova_server.domain.batch.common.service;

import com.nova.nova_server.domain.batch.articleingestion.service.ArticleFlowFactory;
import com.nova.nova_server.domain.batch.summary.service.SummaryStepFactory;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.service.ArticleApiServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchJobService {
    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    private final ArticleApiServiceFactory articleApiServiceFactory;
    private final ArticleFlowFactory articleFlowFactory;
    private final SummaryStepFactory summaryStepFactory;

    public void runArticleIngestionBatch() {
        List<ArticleApiService> articleApiServices = articleApiServiceFactory.createAllAvailableServices();
        Flow flow = articleFlowFactory.createCombinedFlow(articleApiServices);
        Job job = new JobBuilder("articleIngestionBatch", jobRepository)
                .start(flow)
                .build()
                .build();
        runBatch(job);
    }

    public void runSummaryBatch() {
        Step step = summaryStepFactory.createStep();
        Job job = new JobBuilder("summaryBatch", jobRepository)
                .start(step)
                .build();
        runBatch(job);
    }

    public void runArticleIngestionAndSummaryBatch() {
        List<ArticleApiService> articleApiServices = articleApiServiceFactory.createAllAvailableServices();
        Flow flow = articleFlowFactory.createCombinedFlow(articleApiServices);
        Step step = summaryStepFactory.createStep();
        Job job = new JobBuilder("articleIngestionAndSummaryBatch", jobRepository)
                .start(flow)
                .next(step)
                .build()
                .build();
        runBatch(job);
    }

    private void runBatch(Job job) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("runAt", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(job, params);
            log.info("Spring Batch job {} launched", job.getName());
        } catch (Exception e) {
            log.error("Failed to launch job {}", job.getName(), e);
            throw new RuntimeException("Batch job launch failed", e);
        }
    }
}
