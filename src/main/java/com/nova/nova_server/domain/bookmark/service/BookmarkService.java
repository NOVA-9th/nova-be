package com.nova.nova_server.domain.bookmark.service;

import com.nova.nova_server.domain.bookmark.dto.BookmarkInterestCountResponse;
import com.nova.nova_server.domain.bookmark.dto.BookmarkSourceTypeCountResponse;
import com.nova.nova_server.domain.bookmark.repository.CardNewsBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final CardNewsBookmarkRepository bookmarkRepository;
    
    //북마크 interest 통계
    public Map<String, List<BookmarkInterestCountResponse>> getBookmarkCountsByInterest(Long memberId) {
        List<BookmarkInterestCountResponse> counts = bookmarkRepository.findBookmarkCountsByInterest(memberId);
        Map<String, List<BookmarkInterestCountResponse>> result = new HashMap<>();
        result.put("bookmarkCounts", counts);
        return result;
    }
    // 북마크 소스타입 통계
    public Map<String, List<BookmarkSourceTypeCountResponse>> getBookmarkCountsBySourceType(
            Long memberId) {
        List<BookmarkSourceTypeCountResponse> counts = bookmarkRepository
                .findBookmarkCountsBySourceType(memberId);
        Map<String, List<BookmarkSourceTypeCountResponse>> result = new HashMap<>();
        result.put("bookmarkCounts", counts);
        return result;
    }
// 북마크 저장 api: 추후 피드조회 api 구현시 주석해제
//    public List<com.nova.nova_server.domain.cardNews.dto.CardNewsResponse> getBookmarkedCardNews(Long memberId) {
//        List<com.nova.nova_server.domain.cardNews.entity.CardNewsBookmark> bookmarks = bookmarkRepository
//                .findAllByMemberIdWithCardNews(memberId);
//
//        return bookmarks.stream()
//                .map(b -> com.nova.nova_server.domain.cardNews.dto.CardNewsResponse.from(b.getCardNews(),
//                        java.util.Collections.emptyList(), true))
//                .collect(java.util.stream.Collectors.toList());
//    }
}