package com.nova.nova_server.domain.keyword.controller;

import com.nova.nova_server.domain.keyword.dto.KeywordCreateRequest;
import com.nova.nova_server.domain.keyword.dto.KeywordResponse;
import com.nova.nova_server.domain.keyword.dto.KeywordUpdateRequest;
import com.nova.nova_server.domain.keyword.service.KeywordService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "키워드", description = "키워드 CRUD API")
@RestController
@RequestMapping("/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "키워드 생성", description = "새 키워드를 생성합니다.")
    @PostMapping
    public ApiResponse<KeywordResponse> create(@Valid @RequestBody KeywordCreateRequest request) {
        return ApiResponse.created(keywordService.create(request));
    }

    @Operation(summary = "키워드 목록 조회", description = "전체 키워드 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<KeywordResponse>> getAll() {
        return ApiResponse.success(keywordService.getAll());
    }

    @Operation(summary = "키워드 수정", description = "키워드 정보를 수정합니다.")
    @PatchMapping("/{id}")
    public ApiResponse<KeywordResponse> update(
            @Parameter(description = "키워드 ID", required = true) @PathVariable Long id,
            @RequestBody KeywordUpdateRequest request) {
        return ApiResponse.success(keywordService.update(id, request));
    }

    @Operation(summary = "키워드 삭제", description = "키워드를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "키워드 ID", required = true) @PathVariable Long id) {
        keywordService.delete(id);
        return ApiResponse.success(null);
    }
}
