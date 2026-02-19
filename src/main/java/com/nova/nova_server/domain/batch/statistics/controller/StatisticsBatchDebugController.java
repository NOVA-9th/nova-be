package com.nova.nova_server.domain.batch.statistics.controller;

import com.nova.nova_server.domain.batch.statistics.service.StatisticsJobRunner;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/debug/batch")
@Tag(name = "Debug", description = "디버그/테스트 API")
@RequiredArgsConstructor
public class StatisticsBatchDebugController {

    private final StatisticsJobRunner statisticsBatchService;
    private final TaskExecutor flowTaskExecutor;

    @Operation(summary = "statistics batch by range")
    @PostMapping("/statistics-batch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @SuppressWarnings("null")
    public ApiResponse<Void> executeStatisticsBatch(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        flowTaskExecutor.execute(() -> {
            try {
                statisticsBatchService.runStatisticsBatch(
                        Objects.requireNonNull(from),
                        Objects.requireNonNull(to)
                );
            } catch (Exception e) {
                log.error("Statistics batch execution failed", e);
            }
        });
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "statistics batch for min-max range")
    @PostMapping("/statistics-batch/all-range")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<Void> executeStatisticsBatchAllRange() {
        flowTaskExecutor.execute(() -> {
            try {
                boolean started = statisticsBatchService.runStatisticsBatchForAllRange();
                if (!started) {
                    log.info("Skipped statistics batch all-range. No card_news data found.");
                }
            } catch (Exception e) {
                log.error("Statistics all-range batch execution failed", e);
            }
        });
        return ApiResponse.successWithNoData();
    }
}
