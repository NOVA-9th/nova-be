package com.nova.nova_server.domain.batch.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchLaunchService {

    private final JobLauncher jobLauncher;

    public void runBatch(Job job, JobParameters params) {
        try {
            jobLauncher.run(Objects.requireNonNull(job), Objects.requireNonNull(params));
            log.info("Spring Batch job {} launched with params {}", job.getName(), params);
        } catch (Exception e) {
            log.error("Failed to launch job {} with params {}", job.getName(), params, e);
            throw new RuntimeException("Batch job launch failed", e);
        }
    }
}
