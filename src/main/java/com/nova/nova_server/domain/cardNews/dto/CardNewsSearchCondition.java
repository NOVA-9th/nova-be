package com.nova.nova_server.domain.cardNews.dto;

import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.feed.enums.FeedSort;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CardNewsSearchCondition(
        Long memberId,
        FeedSort sort,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<CardType> type,
        List<String> keywords,
        Boolean saved,
        String searchKeyword,
        Boolean hidden,
        Pageable pageable) {
}
