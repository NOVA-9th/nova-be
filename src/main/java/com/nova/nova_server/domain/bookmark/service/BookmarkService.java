package com.nova.nova_server.domain.bookmark.service;

import com.nova.nova_server.domain.bookmark.dto.BookmarkInterestCountResponse;
import com.nova.nova_server.domain.bookmark.dto.BookmarkSourceTypeCountResponse;
import com.nova.nova_server.domain.bookmark.repository.BookmarkAnalyticsRepository;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.entity.CardNewsBookmark;
import com.nova.nova_server.domain.cardNews.repository.CardNewsBookmarkRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.feed.converter.FeedConverter;
import com.nova.nova_server.domain.feed.dto.FeedListResponse;
import com.nova.nova_server.domain.feed.dto.FeedResponse;
import com.nova.nova_server.global.apiPayload.code.error.CommonErrorCode;
import com.nova.nova_server.global.apiPayload.exception.NovaException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkAnalyticsRepository bookmarkAnalyticsRepository;
    private final CardNewsBookmarkRepository bookmarkRepository;
    private final CardNewsRepository cardNewsRepository;
    private final FeedConverter feedConverter;
    private final CardNewsBookmarkRepository cardNewsBookmarkRepository;

    @Transactional
    public void addBookmark(Long memberId, Long cardNewsId) {
        // 이미 북마크 되어있는지 확인
        if (bookmarkRepository.existsByMemberIdAndCardNewsId(memberId, cardNewsId)) {
            return;
        }

        // 카드뉴스 존재 여부 확인
        if (!cardNewsRepository.existsById(cardNewsId)) {
            throw new NovaException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        bookmarkRepository.save(CardNewsBookmark.of(memberId, cardNewsId));
    }

    @Transactional
    public void deleteBookmark(Long memberId, Long cardNewsId) {
        // 카드뉴스 존재 여부 확인
        if (!cardNewsRepository.existsById(cardNewsId)) {
            throw new NovaException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        bookmarkRepository.deleteByMemberIdAndCardNewsId(memberId, cardNewsId);
    }

    public List<FeedResponse> searchBookmarkedCardNews(Long memberId, String title) {
        List<CardNews> cardNewsList = bookmarkRepository.findBookmarkedCardNewsByTitle(memberId,
                title == null ? "" : title);
        return cardNewsList.stream()
                .map(cn -> feedConverter.toResponse(cn, true))
                .toList();
    }

    // 북마크 interest 통계
    public Map<String, List<BookmarkInterestCountResponse>> getBookmarkCountsByInterest(Long memberId) {
        List<BookmarkInterestCountResponse> counts = bookmarkAnalyticsRepository.findBookmarkCountsByInterest(memberId);
        Map<String, List<BookmarkInterestCountResponse>> result = new HashMap<>();
        result.put("bookmarkCounts", counts);
        return result;
    }

    // 북마크 소스타입 통계
    public Map<String, List<BookmarkSourceTypeCountResponse>> getBookmarkCountsBySourceType(
            Long memberId) {
        List<BookmarkSourceTypeCountResponse> counts = bookmarkAnalyticsRepository
                .findBookmarkCountsBySourceType(memberId);
        Map<String, List<BookmarkSourceTypeCountResponse>> result = new HashMap<>();
        result.put("bookmarkCounts", counts);
        return result;
    }

    // 저장함 카드뉴스 검색
    public FeedListResponse searchBookmarkedCardNews(Long memberId, String searchKeyword, Pageable pageable) {
        Page<CardNews> cardNewsList = cardNewsRepository
                .searchBookmarked(memberId, searchKeyword, pageable);

        List<FeedResponse> feedList = cardNewsList.getContent().stream()
                .map(cardNews -> feedConverter.toResponse(cardNews, true)).toList();

        return FeedListResponse.builder()
                .totalCount(cardNewsList.getTotalElements())
                .cardnews(feedList)
                .build();
    }

    // 모든 북마크 삭제
    @Transactional
    public void deleteAllBookmarks(Long memberId) {
        cardNewsBookmarkRepository.deleteAllByMemberId(memberId);
    }
}