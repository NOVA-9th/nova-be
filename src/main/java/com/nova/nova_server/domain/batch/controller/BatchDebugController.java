package com.nova.nova_server.domain.batch.controller;

import com.nova.nova_server.domain.batch.dto.BatchExecutionStatusDto;
import com.nova.nova_server.domain.batch.repository.ArticleEntityRepository;
import com.nova.nova_server.domain.batch.service.BatchJobService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Debug/test controller: run article ingestion batch and check execution status.
 */
@Slf4j
@RestController
@RequestMapping("/debug/batch")
@Tag(name = "Debug: Batch", description = "배치 실행 및 상태 확인 디버그/테스트 API")
public class BatchDebugController {

    private static final String JOB_NAME = "articleIngestionJob";

    private final BatchJobService batchJobService;
    private final JobExplorer jobExplorer;
    private final ArticleEntityRepository articleEntityRepository;
    private final TaskExecutor batchTaskExecutor;

    public BatchDebugController(BatchJobService batchJobService,
                               JobExplorer jobExplorer,
                               ArticleEntityRepository articleEntityRepository,
                               @Qualifier("batchTaskExecutor") TaskExecutor batchTaskExecutor) {
        this.batchJobService = batchJobService;
        this.jobExplorer = jobExplorer;
        this.articleEntityRepository = articleEntityRepository;
        this.batchTaskExecutor = batchTaskExecutor;
    }

    @Operation(summary = "배치 실행", description = "Article ingestion 배치를 비동기로 실행합니다. 즉시 202 응답 후 백그라운드에서 실행됩니다. 상태는 GET /debug/batch/status 로 확인하세요.")
    @PostMapping("/execute")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<String> executeBatch() {
        batchTaskExecutor.execute(() -> {
            try {
                batchJobService.runArticleIngestionBatch();
            } catch (Exception e) {
                log.error("Batch execution failed", e);
            }
        });
        return ApiResponse.success("Job launched: " + JOB_NAME + ". Check GET /debug/batch/status for progress.");
    }

    @Operation(summary = "마지막 배치 실행 상태", description = "articleIngestionJob 의 마지막 실행 상태와 Step 요약을 반환합니다.")
    @GetMapping("/status")
    public ApiResponse<BatchExecutionStatusDto> getLastExecutionStatus() {
        BatchExecutionStatusDto dto = findLastJobExecution();
        return ApiResponse.success(dto);
    }

    @Operation(summary = "저장된 Article 개수", description = "article 테이블에 저장된 기사(ArticleEntity) 개수를 반환합니다.")
    @GetMapping("/article-count")
    public ApiResponse<Long> getArticleCount() {
        return ApiResponse.success(articleEntityRepository.count());
    }

    private BatchExecutionStatusDto findLastJobExecution() {
        int count = 1;
        List<JobInstance> instances = jobExplorer.getJobInstances(JOB_NAME, 0, count);
        if (instances.isEmpty()) {
            return BatchExecutionStatusDto.builder()
                    .jobName(JOB_NAME)
                    .executionId(null)
                    .status("NO_RUN")
                    .exitCode("UNKNOWN")
                    .startTime(null)
                    .endTime(null)
                    .createTime(null)
                    .steps(Collections.emptyList())
                    .build();
        }
        JobExecution execution = jobExplorer.getLastJobExecution(instances.get(0));
        if (execution == null) {
            return BatchExecutionStatusDto.builder()
                    .jobName(JOB_NAME)
                    .executionId(null)
                    .status("NO_RUN")
                    .exitCode("UNKNOWN")
                    .startTime(null)
                    .endTime(null)
                    .createTime(null)
                    .steps(Collections.emptyList())
                    .build();
        }
        return toStatusDto(execution);
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
