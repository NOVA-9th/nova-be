package com.nova.nova_server.domain.feed.dto;

import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.feed.enums.FeedSort;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "피드 조회 요청 파라미터")
public record FeedRequest(
        @Schema(description = "정렬 기준, LATEST 또는 RELEVANCE", example = "RELEVANCE", defaultValue = "LATEST") FeedSort sort,

        @Schema(description = "업로드 기간 (시작), UTC 기준, ISO 8601 형식, 경계값 포함", example = "2026-01-01T15:00:00Z", defaultValue = "2026-01-01T15:00:00Z") OffsetDateTime startDate,

        @Schema(description = "업로드 기간 (종료), UTC 기준, ISO 8601 형식, 경계값 미포함", example = "2026-02-28T15:00:00Z", defaultValue = "2026-02-28T15:00:00Z") OffsetDateTime endDate,

        @Schema(description = "카드 유형, NEWS, JOB, COMMUNITY 중 0개 이상 선택", example = "NEWS") List<CardType> type,

        @Schema(description = "키워드 필터", example = "Spring Boot") List<String> keywords,

        @Schema(description = "페이지 번호", example = "1", defaultValue = "1") Integer page,

        @Schema(description = "페이지 크기", example = "10", defaultValue = "10") Integer size,

        @Schema(description = "저장한 글만 조회할지 여부", example = "false") Boolean saved,

        @Schema(description = "검색 키워드 (제목 또는 본문)", example = "Spring", defaultValue = "") String searchKeyword,

        @Schema(description = "숨김 처리된 글 조회 여부 (true: 숨긴 글만, false: 숨기지 않은 글만)", example = "false", defaultValue = "false") Boolean hidden) {
}
