package com.nova.nova_server.domain.cardNews.dto;

import com.querydsl.core.annotations.QueryProjection;

/**
 * CardNewsRepositoryImpl 내부적으로 사용하는 DTO
 */
public record CardNewsIdScoreResult(
        Long cardNewsId,
        Integer score
) {
    @QueryProjection
    public CardNewsIdScoreResult {
    }
}
