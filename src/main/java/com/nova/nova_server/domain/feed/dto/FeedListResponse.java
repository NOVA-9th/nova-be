package com.nova.nova_server.domain.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "피드 목록 응답")
public record FeedListResponse(
        @Schema(description = "조건에 맞는 전체 카드뉴스 개수", example = "100")
        long totalCount,

        @Schema(description = "카드뉴스 목록")
        List<FeedResponse> cardnews
) {
}
