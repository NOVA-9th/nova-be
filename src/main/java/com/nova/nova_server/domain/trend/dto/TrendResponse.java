package com.nova.nova_server.domain.trend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class TrendResponse {
    private LocalDate baseDate;
    private List<TrendItem> trends;

    @Getter
    @Builder
    public static class TrendItem {
        private int rank;
        private String keyword;
        private long interests;
        private long mentionCount;
        private double growthRate;
        private List<DailyCount> dailyCounts;
    }

    @Getter
    @Builder
    public static class DailyCount {
        private LocalDate date;
        private int count;
    }
}
