package com.nova.nova_server.domain.feed.converter;

import com.nova.nova_server.domain.cardNews.dto.CardNewsSearchCondition;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.feed.dto.FeedRequest;
import com.nova.nova_server.domain.feed.dto.FeedResponse;
import com.nova.nova_server.global.config.FeedConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.lang.Math.min;

@Component
@RequiredArgsConstructor
public class FeedConverter {

    private final FeedConfig feedConfig;

    public CardNewsSearchCondition toCondition(FeedRequest request, Long memberId) {
        return CardNewsSearchCondition.builder()
                .memberId(memberId)
                .sort(request.sort())
                .startDate(toUtcLocalDateTime(request.startDate()))
                .endDate(toUtcLocalDateTime(request.endDate()))
                .type(request.type())
                .keywords(request.keywords())
                .saved(request.saved())
                .pageable(toPageable(request.page(), request.size()))
                .build();
    }

    public FeedResponse toResponse(CardNews cardNews, boolean saved) {
        return FeedResponse.builder()
                .id(cardNews.getId())
                .title(cardNews.getTitle())
                .cardType(cardNews.getCardType())
                .author(cardNews.getAuthor())
                .publishedAt(cardNews.getPublishedAt().atOffset(ZoneOffset.UTC))
                .summary(cardNews.getSummary())
                .evidence(splitEvidence(cardNews.getEvidence()))
                .originalUrl(cardNews.getOriginalUrl())
                .siteName(cardNews.getSourceSiteName())
                .keywords(cardNews.getKeywords().stream()
                        .map(item -> item.getKeyword().getName())
                        .toList())
                .saved(saved)
                .build();
    }

    private List<String> splitEvidence(String evidence) {
        if (evidence == null || evidence.isEmpty()) {
            return List.of();
        }

        return List.of(evidence.split("\\n"));
    }

    private LocalDateTime toUtcLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }

        return offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    private Pageable toPageable(Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = feedConfig.getMaxPageSize();
        } else {
            size = min(size, feedConfig.getMaxPageSize());
        }

        return PageRequest.of(page - 1, size);
    }

}
