package com.nova.nova_server.domain.batch.summary.service;

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
public class SummaryBatchService {

    private final JobRepository jobRepository;
    private final BatchLaunchService batchLaunchService;
    private final SummaryStepFactory summaryStepFactory;

    public void runSummaryBatch() {
        Step step = summaryStepFactory.createStep();
        Job job = new JobBuilder("summaryBatch", Objects.requireNonNull(jobRepository))
                .start(Objects.requireNonNull(step))
                .build();
        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis())
                .toJobParameters();
        batchLaunchService.runBatch(job, params);
    }
}
