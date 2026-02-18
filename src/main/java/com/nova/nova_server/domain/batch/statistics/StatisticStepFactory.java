package com.nova.nova_server.domain.batch.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@RequiredArgsConstructor
public class StatisticStepFactory {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final KeywordStatisticsTasklet keywordStatisticsTasklet;

    public Step createStep() {
        return new StepBuilder("keywordStatisticsStep", jobRepository)
                .tasklet(keywordStatisticsTasklet, transactionManager)
                .build();
    }
}
