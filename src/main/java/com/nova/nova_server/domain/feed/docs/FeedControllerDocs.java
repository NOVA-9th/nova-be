package com.nova.nova_server.domain.feed.docs;

import com.nova.nova_server.domain.feed.dto.FeedListResponse;
import com.nova.nova_server.domain.feed.dto.FeedRequest;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "피드 API")
public interface FeedControllerDocs {

    @Operation(
            summary = "피드 목록 조회",
            description = "쿼리 조건에 따라 피드 목록을 조회합니다."
    )
    ResponseEntity<ApiResponse<FeedListResponse>> getCardNews(
            @AuthenticationPrincipal long memberId,
            @Parameter(description = "피드 조회 필터")
            @ModelAttribute FeedRequest request
    );
}
