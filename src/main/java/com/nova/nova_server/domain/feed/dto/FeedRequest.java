package com.nova.nova_server.domain.feed.dto;

import com.nova.nova_server.domain.feed.enums.FeedSort;
import com.nova.nova_server.domain.post.model.CardType;

import java.time.OffsetDateTime;
import java.util.List;

public record FeedRequest(
        FeedSort sort,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        CardType type,
        List<String> keywords,
        Integer page,
        Integer size,
        Boolean saved
) {
}
