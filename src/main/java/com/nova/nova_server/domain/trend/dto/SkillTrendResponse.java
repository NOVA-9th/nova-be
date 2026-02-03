package com.nova.nova_server.domain.trend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SkillTrendResponse {
    private LocalDate baseDate;
    private List<RankingItem> rankings;

    @Getter
    @Builder
    public static class RankingItem {
        private int rank;
        private Long interest;
        private long totalMentionCount;
        private List<String> keywords;
    }
}