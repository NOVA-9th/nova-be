package com.nova.nova_server.domain.batch.cardnews.service;

import com.nova.nova_server.domain.batch.common.service.BatchLaunchService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CardNewsBatchProcessingJobRunner {

    private final JobRepository jobRepository;
    private final BatchLaunchService batchLaunchService;
    private final CardNewsBatchProcessingStepFactory cardNewsBatchProcessingStepFactory;

    public void runCardNewsBatchProcessingJob(String batchId) {
        Step step = cardNewsBatchProcessingStepFactory.createStep();
        Job job = new JobBuilder("cardNewsBatchProcessingJob", Objects.requireNonNull(jobRepository))
                .start(Objects.requireNonNull(step))
                .build();
        JobParameters params = new JobParametersBuilder()
                .addString("batchId", Objects.requireNonNull(batchId))
                .toJobParameters();
        batchLaunchService.runBatch(job, params);
    }
}
