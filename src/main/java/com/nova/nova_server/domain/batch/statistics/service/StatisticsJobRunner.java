package com.nova.nova_server.domain.batch.statistics.service;

import com.nova.nova_server.domain.batch.common.service.BatchLaunchService;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatisticsJobRunner {

    private final JobRepository jobRepository;
    private final BatchLaunchService batchLaunchService;
    private final StatisticStepFactory statisticStepFactory;
    private final CardNewsRepository cardNewsRepository;

    public void runDailyStatisticsBatch(LocalDate date) {
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.plusDays(1).atStartOfDay();
        runStatisticsBatch(startTime, endTime);
    }

    public void runStatisticsBatch(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime safeStartTime = Objects.requireNonNull(startTime);
        LocalDateTime safeEndTime = Objects.requireNonNull(endTime);
        Step step = statisticStepFactory.createStep(safeStartTime, safeEndTime);
        Job job = new JobBuilder("statisticsBatch", Objects.requireNonNull(jobRepository))
                .start(Objects.requireNonNull(step))
                .build();
        JobParameters params = new JobParametersBuilder()
                .addString("startDate", Objects.requireNonNull(safeStartTime.toString()))
                .addString("endDate", Objects.requireNonNull(safeEndTime.toString()))
                .addString("runAt", Objects.requireNonNull(LocalDateTime.now().toString()))
                .toJobParameters();
        batchLaunchService.runBatch(job, params);
    }

    public boolean runStatisticsBatchForAllRange() {
        LocalDateTime minCreatedAt = cardNewsRepository.findMinCreatedAt().orElse(null);
        LocalDateTime maxCreatedAt = cardNewsRepository.findMaxCreatedAt().orElse(null);

        if (minCreatedAt == null || maxCreatedAt == null) {
            return false;
        }

        runStatisticsBatch(minCreatedAt, maxCreatedAt.plusNanos(1));
        return true;
    }
}
