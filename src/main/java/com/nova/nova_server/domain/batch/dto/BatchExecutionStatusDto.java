package com.nova.nova_server.domain.batch.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Debug/test: last job execution status and step summaries.
 */
@Getter
@Builder
public class BatchExecutionStatusDto {

    private final String jobName;
    private final Long executionId;
    private final String status;       // STARTING, STARTED, COMPLETED, FAILED, etc.
    private final String exitCode;     // COMPLETED, FAILED, UNKNOWN
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final LocalDateTime createTime;
    private final List<StepExecutionSummaryDto> steps;

    @Getter
    @Builder
    public static class StepExecutionSummaryDto {
        private final String stepName;
        private final String status;
        private final String exitCode;
        private final Long readCount;
        private final Long writeCount;
        private final Long skipCount;
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
    }
}
