package com.nova.nova_server.domain.batch.controller;

import com.nova.nova_server.domain.batch.service.CardNewsBatchService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트나 개발용으로 배치를 직접 실행해볼 수 있는 API입니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
@Tag(name = "Batch", description = "배치 작업 API")
public class BatchController {

    private final CardNewsBatchService cardNewsBatchService;

    @PostMapping("/card-news/execute")
    @Operation(summary = "카드뉴스 배치 수동 실행", description = "카드뉴스 수집 및 LLM 요약 배치 작업을 수동으로 실행합니다.")
    public ApiResponse<String> executeCardNewsBatch() {
        log.info("Manual CardNews batch triggered via API");
        cardNewsBatchService.executeBatch();
        return ApiResponse.success("배치 작업이 백그라운드에서 시작되었습니다. 완료까지 시간이 소요될 수 있습니다.");
    }
}
