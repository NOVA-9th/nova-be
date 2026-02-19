package com.nova.nova_server.domain.batch.articleingestion.service;

import com.nova.nova_server.domain.batch.common.service.BatchLaunchService;
import com.nova.nova_server.domain.batch.summary.service.SummaryStepFactory;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.service.ArticleApiServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArticleIngestionJobRunner {

    private final JobRepository jobRepository;
    private final BatchLaunchService batchLaunchService;
    private final ArticleApiServiceFactory articleApiServiceFactory;
    private final ArticleFlowFactory articleFlowFactory;
    private final SummaryStepFactory summaryStepFactory;

    public void runArticleIngestionBatch() {
        List<ArticleApiService> articleApiServices = articleApiServiceFactory.createAllAvailableServices();
        Flow flow = articleFlowFactory.createCombinedFlow(articleApiServices);
        Job job = new JobBuilder("articleIngestionBatch", Objects.requireNonNull(jobRepository))
                .start(Objects.requireNonNull(flow))
                .build()
                .build();
        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis())
                .toJobParameters();
        batchLaunchService.runBatch(job, params);
    }

    public void runArticleIngestionAndSummaryBatch() {
        List<ArticleApiService> articleApiServices = articleApiServiceFactory.createAllAvailableServices();
        Flow flow = articleFlowFactory.createCombinedFlow(articleApiServices);
        Step summaryStep = summaryStepFactory.createStep();

        Job job = new JobBuilder("articleIngestionAndSummaryBatch", Objects.requireNonNull(jobRepository))
                .start(Objects.requireNonNull(flow))
                .next(Objects.requireNonNull(summaryStep))
                .build()
                .build();
        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis())
                .toJobParameters();
        batchLaunchService.runBatch(job, params);
    }
}
