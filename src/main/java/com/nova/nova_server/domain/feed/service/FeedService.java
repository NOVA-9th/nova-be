package com.nova.nova_server.domain.feed.service;

import com.nova.nova_server.domain.cardNews.dto.CardNewsScoreResult;
import com.nova.nova_server.domain.cardNews.dto.CardNewsSearchCondition;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.repository.CardNewsBookmarkRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsHiddenRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.feed.converter.FeedConverter;
import com.nova.nova_server.domain.feed.dto.FeedListResponse;
import com.nova.nova_server.domain.feed.dto.FeedRequest;
import com.nova.nova_server.domain.feed.dto.FeedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class FeedService {

    private final CardNewsRepository cardNewsRepository;

    private final CardNewsBookmarkRepository bookmarkRepository;

    private final CardNewsHiddenRepository hiddenRepository;

    private final FeedConverter feedConverter;

    public FeedListResponse getCardNews(
            long memberId,
            FeedRequest request
    ) {
        CardNewsSearchCondition condition = feedConverter.toCondition(request, memberId);
        Page<CardNewsScoreResult> resultList = cardNewsRepository.searchByCondition(condition);
        List<Long> cardNewsIds = resultList.stream().map(CardNewsScoreResult::cardNews).map(CardNews::getId).toList();
        Set<Long> bookmarkedCardNewsIds = bookmarkRepository.filterBookmarkedCardNewsIds(memberId, cardNewsIds);

        List<FeedResponse> feedList = resultList.getContent().stream().map(result ->
                feedConverter.toResponse(
                        result.cardNews(),
                        result.score(),
                        bookmarkedCardNewsIds.contains(result.cardNews().getId()),
                        hiddenRepository.existsByMemberIdAndCardNewsId(memberId, result.cardNews().getId())
                )
        ).toList();

        return FeedListResponse.builder()
                .totalCount(resultList.getTotalElements())
                .cardnews(feedList)
                .build();
    }

}
