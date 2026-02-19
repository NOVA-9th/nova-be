package com.nova.nova_server.domain.batch.statistics.service;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StatisticStepFactory {
    private static final int CHUNK_SIZE = 200;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CardNewsKeywordReader cardNewsKeywordReader;
    private final KeywordStatisticsWriter keywordStatisticsWriter;

    public Step createStep(LocalDateTime startTime, LocalDateTime endTime) {
        return new StepBuilder("keywordStatisticsStep", Objects.requireNonNull(jobRepository))
                .<CardNews, CardNews>chunk(CHUNK_SIZE, Objects.requireNonNull(transactionManager))
                .reader(Objects.requireNonNull(cardNewsKeywordReader.create(startTime, endTime)))
                .writer(Objects.requireNonNull(keywordStatisticsWriter))
                .build();
    }
}
