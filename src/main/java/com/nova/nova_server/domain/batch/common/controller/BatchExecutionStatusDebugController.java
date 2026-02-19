package com.nova.nova_server.domain.batch.common.controller;

import com.nova.nova_server.domain.batch.common.dto.BatchExecutionStatusDto;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug/batch")
@Tag(name = "Debug", description = "디버그/테스트 API")
@RequiredArgsConstructor
public class BatchExecutionStatusDebugController {

    private final JobExplorer jobExplorer;

    @Operation(summary = "모든 배치 실행 결과", description = "등록된 모든 Job 의 실행 이력(JobExecution)을 최신순으로 반환합니다.")
    @GetMapping("/status/all")
    public ApiResponse<List<BatchExecutionStatusDto>> getAllBatchResults() {
        List<BatchExecutionStatusDto> results = findAllJobExecutions();
        return ApiResponse.success(results);
    }

    private List<BatchExecutionStatusDto> findAllJobExecutions() {
        int maxInstancesPerJob = 100;
        return jobExplorer.getJobNames().stream()
                .flatMap(jobName -> jobExplorer.getJobInstances(Objects.requireNonNull(jobName), 0, maxInstancesPerJob).stream())
                .flatMap(instance -> jobExplorer.getJobExecutions(Objects.requireNonNull(instance)).stream())
                .sorted(Comparator.comparing(JobExecution::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(BatchExecutionStatusDebugController::toStatusDto)
                .collect(Collectors.toList());
    }

    private static BatchExecutionStatusDto toStatusDto(JobExecution execution) {
        List<BatchExecutionStatusDto.StepExecutionSummaryDto> steps = execution.getStepExecutions().stream()
                .map(se -> BatchExecutionStatusDto.StepExecutionSummaryDto.builder()
                        .stepName(se.getStepName())
                        .status(se.getStatus().name())
                        .exitCode(se.getExitStatus().getExitCode())
                        .readCount(se.getReadCount())
                        .writeCount(se.getWriteCount())
                        .skipCount(se.getSkipCount())
                        .startTime(se.getStartTime())
                        .endTime(se.getEndTime())
                        .build())
                .collect(Collectors.toList());

        return BatchExecutionStatusDto.builder()
                .jobName(execution.getJobInstance().getJobName())
                .executionId(execution.getId())
                .status(execution.getStatus().name())
                .exitCode(execution.getExitStatus().getExitCode())
                .startTime(execution.getStartTime())
                .endTime(execution.getEndTime())
                .createTime(execution.getCreateTime())
                .steps(steps)
                .build();
    }
}
