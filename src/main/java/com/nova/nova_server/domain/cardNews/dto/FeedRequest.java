package com.nova.nova_server.domain.cardNews.dto;

import com.nova.nova_server.domain.cardNews.enums.FeedSort;
import com.nova.nova_server.domain.post.model.CardType;

import java.util.List;

public record FeedRequest(
        FeedSort sort,
        Integer duration,
        CardType type,
        List<String> keywords,
        Integer page,
        Integer size,
        Boolean saved
) {
}
