package com.nova.nova_server.domain.feed.dto;

import com.nova.nova_server.domain.post.model.CardType;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record FeedResponse(
        long id,
        String title,
        CardType cardType,
        String author,
        OffsetDateTime publishedAt,
        String summary,
        String evidence,
        String originalUrl,
        String siteName,
        List<String> keywords,
        boolean saved
) {
}
