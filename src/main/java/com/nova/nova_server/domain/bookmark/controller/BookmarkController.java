package com.nova.nova_server.domain.bookmark.controller;

import com.nova.nova_server.domain.bookmark.dto.BookmarkInterestCountResponse;
import com.nova.nova_server.domain.bookmark.dto.BookmarkSourceTypeCountResponse;
import com.nova.nova_server.domain.bookmark.service.BookmarkService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    public ApiResponse<Map<String, List<BookmarkSourceTypeCountResponse>>> getBookmarkCountsBySourceType(@AuthenticationPrincipal Long memberId) {

        return ApiResponse.success(bookmarkService.getBookmarkCountsBySourceType(memberId));
    }
}