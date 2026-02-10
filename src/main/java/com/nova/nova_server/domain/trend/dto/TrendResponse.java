package com.nova.nova_server.domain.trend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@io.swagger.v3.oas.annotations.media.Schema(description = "실시간 트렌드 키워드 응답")
public class TrendResponse {
    @io.swagger.v3.oas.annotations.media.Schema(description = "기준 날짜", example = "2024-02-09")
    private LocalDate baseDate;
    private List<TrendItem> trends;

    @Getter
    @Builder
    @io.swagger.v3.oas.annotations.media.Schema(description = "트렌드 항목")
    public static class TrendItem {
        @io.swagger.v3.oas.annotations.media.Schema(description = "순위", example = "1")
        private int rank;
        @io.swagger.v3.oas.annotations.media.Schema(description = "키워드", example = "Spring Boot")
        private String keyword;
        @io.swagger.v3.oas.annotations.media.Schema(description = "관심사 ID", example = "1")
        private long interests;
        @io.swagger.v3.oas.annotations.media.Schema(description = "언급량", example = "150")
        private long mentionCount;
        @io.swagger.v3.oas.annotations.media.Schema(description = "전주 대비 성장률 (%)", example = "12.5")
        private double growthRate;
        private List<DailyCount> dailyCounts;
    }

    @Getter
    @Builder
    @io.swagger.v3.oas.annotations.media.Schema(description = "일별 언급량 통계")
    public static class DailyCount {
        @io.swagger.v3.oas.annotations.media.Schema(description = "날짜", example = "2024-02-08")
        private LocalDate date;
        @io.swagger.v3.oas.annotations.media.Schema(description = "언급 횟수", example = "20")
        private int count;
    }
}
