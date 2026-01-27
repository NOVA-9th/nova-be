package com.nova.nova_server.domain.feed.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FeedListResponse(
        long totalCount,
        List<FeedResponse> cardnews
) {
}
