package com.nova.nova_server.domain.batch.cardnews.service;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@RequiredArgsConstructor
public class CardNewsBatchProcessingStepFactory {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CardNewsBatchProcessingTasklet cardNewsBatchProcessingTasklet;

    public Step createStep() {
        return new StepBuilder("cardNewsBatchProcessingStep", jobRepository)
                .tasklet(cardNewsBatchProcessingTasklet, transactionManager)
                .build();
    }
}
