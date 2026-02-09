package com.nova.nova_server.domain.cardNews.dto;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import lombok.Builder;

@Builder
public record CardNewsScoreResult(
        CardNews cardNews,
        Integer score
) {
}
