package com.nova.nova_server.domain.batch.summary.service;

import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Component
@RequiredArgsConstructor
public class SummaryStepFactory {
    private static final int CHUNK_SIZE = 100;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ArticleEntityReader articleEntityReader;
    private final ArticleSummaryWriter articleSummaryWriter;

    public Step createStep() {
        return new StepBuilder("aiSummaryStep", jobRepository)
                .<ArticleEntity, ArticleEntity>chunk(CHUNK_SIZE, transactionManager)
                .reader(articleEntityReader)
                .writer(articleSummaryWriter)
                .build();
    }
}
