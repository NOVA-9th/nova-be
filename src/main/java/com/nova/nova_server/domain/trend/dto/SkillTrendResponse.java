package com.nova.nova_server.domain.trend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@io.swagger.v3.oas.annotations.media.Schema(description = "관심사별 기술 스택 트렌드 응답")
public class SkillTrendResponse {
    private List<RankingItem> rankings;

    @Getter
    @Builder
    @io.swagger.v3.oas.annotations.media.Schema(description = "관심사별 순위 항목")
    public static class RankingItem {
        @io.swagger.v3.oas.annotations.media.Schema(description = "순위", example = "1")
        private int rank;
        @io.swagger.v3.oas.annotations.media.Schema(description = "관심사 ID", example = "1")
        private Long interest;
        @io.swagger.v3.oas.annotations.media.Schema(description = "총 언급량", example = "500")
        private long totalMentionCount;
        private List<KeywordItem> keywords;
    }

    @Getter
    @Builder
    @io.swagger.v3.oas.annotations.media.Schema(description = "키워드 정보")
    public static class KeywordItem {
        @io.swagger.v3.oas.annotations.media.Schema(description = "키워드 이름", example = "Java")
        private String name;
        @io.swagger.v3.oas.annotations.media.Schema(description = "언급량", example = "100")
        private long mentionCount;
    }
}