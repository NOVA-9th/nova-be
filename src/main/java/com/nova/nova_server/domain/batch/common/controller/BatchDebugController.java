package com.nova.nova_server.domain.batch.common.controller;

import com.nova.nova_server.domain.batch.common.dto.BatchExecutionStatusDto;
import com.nova.nova_server.domain.batch.common.service.BatchJobService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Debug/test controller: run article ingestion batch and check execution status.
 */
@Slf4j
@RestController
@RequestMapping("/debug/batch")
@Tag(name = "Debug: Batch", description = "배치 실행 및 상태 확인 디버그/테스트 API")
@RequiredArgsConstructor
public class BatchDebugController {
    private final BatchJobService batchJobService;
    private final JobExplorer jobExplorer;
    private final TaskExecutor flowTaskExecutor;

    @Operation(summary = "article ingestion batch")
    @PostMapping("/article-ingestion-batch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<Void> executeArticleIngestionBatch() {
        flowTaskExecutor.execute(() -> {
            try {
                batchJobService.runArticleIngestionBatch();
            } catch (Exception e) {
                log.error("Batch execution failed", e);
            }
        });
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "summary batch")
    @PostMapping("/summary-batch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<Void> executeSummaryBatch() {
        flowTaskExecutor.execute(() -> {
            try {
                batchJobService.runSummaryBatch();
            } catch (Exception e) {
                log.error("Batch execution failed", e);
            }
        });
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "full batch")
    @PostMapping("/full-batch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<Void> executeFullBatch() {
        flowTaskExecutor.execute(() -> {
            try {
                batchJobService.runArticleIngestionAndSummaryBatch();
            } catch (Exception e) {
                log.error("Batch execution failed", e);
            }
        });
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "모든 배치 실행 결과", description = "등록된 모든 Job 의 실행 이력(JobExecution)을 최신순으로 반환합니다.")
    @GetMapping("/status/all")
    public ApiResponse<List<BatchExecutionStatusDto>> getAllBatchResults() {
        List<BatchExecutionStatusDto> results = findAllJobExecutions();
        return ApiResponse.success(results);
    }

    private List<BatchExecutionStatusDto> findAllJobExecutions() {
        int maxInstancesPerJob = 100;
        return jobExplorer.getJobNames().stream()
                .flatMap(jobName -> jobExplorer.getJobInstances(jobName, 0, maxInstancesPerJob).stream())
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .sorted(Comparator.comparing(JobExecution::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(BatchDebugController::toStatusDto)
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
