package com.nova.nova_server.domain.bookmark.controller;

import com.nova.nova_server.domain.bookmark.dto.BookmarkInterestCountResponse;
import com.nova.nova_server.domain.bookmark.dto.BookmarkSourceTypeCountResponse;
import com.nova.nova_server.domain.bookmark.service.BookmarkService;
import com.nova.nova_server.domain.feed.dto.FeedListResponse;
import com.nova.nova_server.domain.feed.dto.FeedResponse;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
@Tag(name = "Bookmark", description = "북마크 관련 API")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "관심사별 북마크 개수 조회", description = "사용자가 북마크한 카드뉴스의 개수를 관심사별로 집계하여 반환합니다.")
    @GetMapping("/interests/counts")
    public ApiResponse<Map<String, List<BookmarkInterestCountResponse>>> getBookmarkCounts(
            @AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(bookmarkService.getBookmarkCountsByInterest(memberId));
    }

    @Operation(summary = "출처별 북마크 개수 조회", description = "사용자가 북마크한 카드뉴스의 개수를 출처(뉴스, 커뮤니티 등)별로 집계하여 반환합니다.")
    @GetMapping("/sourcetype/counts")
    public ApiResponse<Map<String, List<BookmarkSourceTypeCountResponse>>> getBookmarkCountsBySourceType(
            @AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(bookmarkService.getBookmarkCountsBySourceType(memberId));
    }

    @Operation(summary = "카드뉴스 북마크 추가", description = "특정 카드뉴스를 북마크에 추가합니다.")
    @PostMapping("/{cardnewsId}")
    public ApiResponse<Void> addBookmark(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long cardnewsId) {
        bookmarkService.addBookmark(memberId, cardnewsId);
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "카드뉴스 북마크 삭제", description = "특정 카드뉴스를 북마크에서 삭제합니다.")
    @DeleteMapping("/{cardnewsId}")
    public ApiResponse<Void> deleteBookmark(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long cardnewsId) {
        bookmarkService.deleteBookmark(memberId, cardnewsId);
        return ApiResponse.successWithNoData();
    }

//    @Operation(summary = "북마크 내 카드뉴스 제목 검색", description = "북마크한 카드뉴스 중 제목에 검색어가 포함된 목록을 반환합니다.")
//    @GetMapping
//    public ApiResponse<List<FeedResponse>> searchBookmarks(
//            @AuthenticationPrincipal Long memberId,
//            @Parameter(description = "검색할 카드뉴스 제목") @RequestParam(required = false) String title) {
//        return ApiResponse.success(bookmarkService.searchBookmarkedCardNews(memberId, title));
//    }

    @Operation(summary = "저장된 글 검색 및 조회", description = "사용자가 저장한 글을 검색어로 조회합니다. 검색어가 없으면 전체 저장된 글을 조회합니다.")
    @GetMapping("/search")
    public ApiResponse<FeedListResponse> getBookmarkedCardNews(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) String searchKeyword,
            @PageableDefault() Pageable pageable) {
        return ApiResponse.success(bookmarkService.searchBookmarkedCardNews(memberId, searchKeyword, pageable));
    }

    @Operation(summary = "모든 북마크 삭제", description = "사용자가 저장한 모든 카드뉴스를 삭제합니다.")
    @DeleteMapping("/delete/all")
    public ApiResponse<Void> deleteAllBookmarks(@AuthenticationPrincipal Long memberId) {
        bookmarkService.deleteAllBookmarks(memberId);
        return ApiResponse.success(null);
    }
}