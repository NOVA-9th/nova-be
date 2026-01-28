package com.nova.nova_server.domain.ai.controller;


import com.nova.nova_server.domain.ai.exception.AiException;
import com.nova.nova_server.domain.ai.service.AiBatchService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import com.nova.nova_server.global.apiPayload.code.error.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Profile("local")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/test")
public class AiTestController {

    private final AiBatchService aiBatchService;

    @PostMapping("/batch")
    public ApiResponse<String> createBatchTest(@RequestBody(required = false) List<String> prompts) {
        if (prompts == null || prompts.isEmpty()) {
            prompts = List.of(
                    "네 이름은 뭐야?",
                    "Hello world라고 응답해줘.",
                    "네 이름은 뭐야?"  // temperature 설정 확인용 동일 요청
            );
        }
        return ApiResponse.created(aiBatchService.createBatch(prompts));
    }

    @GetMapping("/batch/{batchId}/status")
    public ApiResponse<Boolean> isCompleteTest(@PathVariable String batchId) {
        return ApiResponse.success(aiBatchService.isCompleted(batchId));
    }

    @GetMapping("/batch/{batchId}/result")
    public ApiResponse<List<String>> getResultListTest(@PathVariable String batchId) {
        return ApiResponse.success(aiBatchService.getResultList(batchId));
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
