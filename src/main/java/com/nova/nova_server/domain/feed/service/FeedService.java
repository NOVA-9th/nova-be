package com.nova.nova_server.domain.feed.service;

import com.nova.nova_server.domain.cardNews.dto.CardNewsSearchCondition;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.repository.CardNewsBookmarkRepository;
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

    private final FeedConverter feedConverter;

    public FeedListResponse getCardNews(
            long memberId,
            FeedRequest request
    ) {
        CardNewsSearchCondition condition = feedConverter.toCondition(request, memberId);
        Page<CardNews> cardNewsList = cardNewsRepository.searchByCondition(condition);
        List<Long> cardNewsIds = cardNewsList.stream().map(CardNews::getId).toList();
        Set<Long> bookmarkedCardNewsIds = bookmarkRepository.filterBookmarkedCardNewsIds(memberId, cardNewsIds);

        List<FeedResponse> feedList = cardNewsList.getContent().stream().map(cardNews ->
                feedConverter.toResponse(cardNews, bookmarkedCardNewsIds.contains(cardNews.getId()))
        ).toList();

        return FeedListResponse.builder()
                .totalCount(cardNewsList.getTotalElements())
                .cardnews(feedList)
                .build();
    }

}
