package com.nova.nova_server.domain.batch.summary.controller;

import com.nova.nova_server.domain.batch.summary.service.SummaryBatchService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/debug/batch")
@Tag(name = "Debug", description = "디버그/테스트 API")
@RequiredArgsConstructor
public class SummaryBatchDebugController {

    private final SummaryBatchService summaryBatchService;
    private final TaskExecutor flowTaskExecutor;

    @Operation(summary = "summary batch")
    @PostMapping("/summary-batch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<Void> executeSummaryBatch() {
        flowTaskExecutor.execute(() -> {
            try {
                summaryBatchService.runSummaryBatch();
            } catch (Exception e) {
                log.error("Summary batch execution failed", e);
            }
        });
        return ApiResponse.successWithNoData();
    }
}
