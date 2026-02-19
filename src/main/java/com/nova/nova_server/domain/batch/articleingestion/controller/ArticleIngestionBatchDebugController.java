package com.nova.nova_server.domain.batch.articleingestion.controller;

import com.nova.nova_server.domain.batch.articleingestion.service.ArticleIngestionJobRunner;
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
public class ArticleIngestionBatchDebugController {

    private final ArticleIngestionJobRunner articleIngestionBatchService;
    private final TaskExecutor flowTaskExecutor;

    @Operation(summary = "article ingestion batch")
    @PostMapping("/article-ingestion-batch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<Void> executeArticleIngestionBatch() {
        flowTaskExecutor.execute(() -> {
            try {
                articleIngestionBatchService.runArticleIngestionBatch();
            } catch (Exception e) {
                log.error("Article ingestion batch execution failed", e);
            }
        });
        return ApiResponse.successWithNoData();
    }
}
