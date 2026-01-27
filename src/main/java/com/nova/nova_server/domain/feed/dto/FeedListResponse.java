package com.nova.nova_server.domain.feed.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FeedListResponse(
        int totalCount,
        List<FeedResponse> cardnews
) {
}
