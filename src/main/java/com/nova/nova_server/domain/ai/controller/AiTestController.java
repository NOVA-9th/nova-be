package com.nova.nova_server.domain.ai.controller;


import com.nova.nova_server.domain.ai.exception.AiException;
import com.nova.nova_server.domain.ai.service.AiBatchService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import com.nova.nova_server.global.apiPayload.code.error.CommonErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Profile("local")
@RestController
@RequiredArgsConstructor
@RequestMapping("/debug/ai/test")
@Tag(name = "Debug", description = "디버그/테스트 API")
public class AiTestController {

    private final AiBatchService aiBatchService;

    @PostMapping("/batch")
    public ApiResponse<String> createBatchTest(@RequestBody(required = false) Map<String, String> prompts) {
        if (prompts == null || prompts.isEmpty()) {
            prompts = Map.of(
                    "request-1", "네 이름은 뭐야?",
                    "request-2", "Hello world라고 응답해줘.",
                    "request-3", "네 이름은 뭐야?"  // temperature 설정 확인용 동일 요청
            );
        }
        return ApiResponse.created(aiBatchService.createBatch(prompts));
    }

    @GetMapping("/batch/{batchId}/status")
    public ApiResponse<Boolean> isCompleteTest(@PathVariable String batchId) {
        return ApiResponse.success(aiBatchService.isCompleted(batchId));
    }

    @GetMapping("/batch/{batchId}/result")
    public ApiResponse<Map<String, String>> getResultListTest(@PathVariable String batchId) {
        return ApiResponse.success(aiBatchService.getResults(batchId));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleException(AiException.InvalidBatchIdException e) {
        return ApiResponse.fail(CommonErrorCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleException(AiException.InvalidBatchInputException e) {
        return ApiResponse.fail(CommonErrorCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<?> handleException(AiException.PendingBatchException e) {
        return ApiResponse.fail(CommonErrorCode.CONFLICT, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(AiException e) {
        return ApiResponse.fail(CommonErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
