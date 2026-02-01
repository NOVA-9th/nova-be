package com.nova.nova_server.domain.feed.dto;

import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.feed.enums.FeedSort;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "피드 조회 요청 파라미터")
public record FeedRequest(
        @Schema(description = "정렬 기준, LATEST 또는 RELEVANCE", example = "RELEVANCE", defaultValue = "LATEST")
        FeedSort sort,

        @Schema(description = "업로드 기간 (시작), UTC 기준, ISO 8601 형식, 경계값 포함", example = "2026-01-01T15:00:00Z")
        OffsetDateTime startDate,

        @Schema(description = "업로드 기간 (종료), UTC 기준, ISO 8601 형식, 경계값 미포함", example = "2026-01-31T15:00:00Z")
        OffsetDateTime endDate,

        @Schema(description = "카드 유형, NEWS, JOB, COMMUNITY 중 0개 이상 선택", example = "[\"NEWS\", \"COMMUNITY\"]")
        List<CardType> type,

        @Schema(description = "키워드 필터", example = "[\"Spring Boot\", \"React\"]")
        List<String> keywords,

        @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
        Integer size,

        @Schema(description = "저장한 글만 조회할지 여부", example = "false")
        Boolean saved
) {
}
