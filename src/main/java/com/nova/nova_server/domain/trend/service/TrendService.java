package com.nova.nova_server.domain.trend.service;

import com.nova.nova_server.domain.keyword.entity.KeywordStatistics;
import com.nova.nova_server.domain.keyword.repository.KeywordStatisticsRepository;
import com.nova.nova_server.domain.trend.dto.TrendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendService {

    private final KeywordStatisticsRepository keywordStatisticsRepository;

    public TrendResponse getTopKeywords(LocalDate baseDate) {
        // 오늘 포함 최근 7일: [baseDate-6, baseDate]
        LocalDate startDate = baseDate.minusDays(6);
        // 지난 주: [startDate-7, startDate-1]
        LocalDate prevWeekStart = startDate.minusDays(7);
        // 성장률 계산을 위해 지난 주 통계까지 조회
        // 조회 범위: [prevWeekStart ~ endDate]

        // 이번 주(startDate ~ endDate) Top 10 키워드 ID 조회
        List<Long> topKeywordIds = keywordStatisticsRepository.findTopKeywordIds(startDate, baseDate,
                PageRequest.of(0, 10));

        if (topKeywordIds.isEmpty()) {
            return TrendResponse.builder()
                    .baseDate(baseDate)
                    .trends(Collections.emptyList())
                    .build();
        }

        // 해당 키워드들에 대한 전체 통계 조회 (지난 주 + 이번 주)
        // 범위: prevWeekStart ~ endDate
        List<KeywordStatistics> stats = keywordStatisticsRepository.findByKeywordIdsInAndStatDateBetween(
                topKeywordIds, prevWeekStart, baseDate);

        // 데이터 처리
        // 키워드 ID별 그룹화
        Map<Long, List<KeywordStatistics>> statsByKeyword = stats.stream()
                .collect(Collectors.groupingBy(ks -> ks.getKeyword().getId()));

        List<TrendResponse.TrendItem> trendItems = new ArrayList<>();

        int rank = 1;
        // 첫 번째 쿼리의 정렬 순서를 유지하기 위해 topKeywordIds 순회
        for (Long keywordId : topKeywordIds) {
            List<KeywordStatistics> keywordStats = statsByKeyword.getOrDefault(keywordId, Collections.emptyList());
            if (keywordStats.isEmpty())
                continue;

            // 합계 계산
            long currentWeekSum = keywordStats.stream()
                    .filter(ks -> !ks.getStatDate().isBefore(startDate) && !ks.getStatDate().isAfter(baseDate))
                    .mapToLong(KeywordStatistics::getMentionCount)
                    .sum();

            long prevWeekSum = keywordStats.stream()
                    .filter(ks -> !ks.getStatDate().isBefore(prevWeekStart) && ks.getStatDate().isBefore(startDate))
                    .mapToLong(KeywordStatistics::getMentionCount)
                    .sum();

            // 성장률 계산
            double growthRate = 0.0;
            if (prevWeekSum > 0) {
                growthRate = ((double) (currentWeekSum - prevWeekSum) / prevWeekSum) * 100.0;
            } else if (currentWeekSum > 0) {
                // 이전 값이 0이고 현재 값이 0보다 크면 100% 성장으로 간주 (100.0으로 설정)
                growthRate = 100.0;
            }

            // 이번 주 일별 통계
            List<TrendResponse.DailyCount> dailyCounts = new ArrayList<>();
            Map<LocalDate, Integer> dateToCountMap = keywordStats.stream()
                    .filter(ks -> !ks.getStatDate().isBefore(startDate))
                    .collect(Collectors.toMap(KeywordStatistics::getStatDate, KeywordStatistics::getMentionCount,
                            (a, b) -> a));

            for (int i = 0; i < 7; i++) {
                LocalDate d = startDate.plusDays(i);
                dailyCounts.add(TrendResponse.DailyCount.builder()
                        .date(d)
                        .count(dateToCountMap.getOrDefault(d, 0))
                        .build());
            }

            // 키워드 정보
            var firstStat = keywordStats.get(0);
            String keywordName = firstStat.getKeyword().getName();

            trendItems.add(TrendResponse.TrendItem.builder()
                    .rank(rank++)
                    .keyword(keywordName)
                    .interests(firstStat.getKeyword().getInterest().getId())
                    .mentionCount(currentWeekSum)
                    .growthRate(Math.round(growthRate * 10.0) / 10.0)
                    .dailyCounts(dailyCounts)
                    .build());
        }

        return TrendResponse.builder()
                .baseDate(baseDate)
                .trends(trendItems)
                .build();
    }

    public com.nova.nova_server.domain.trend.dto.SkillTrendResponse getSkillTrend() {

        // 전체 기간 동안의 각 Interest 별 총 언급량 집계 및 정렬 (내림차순)
        List<Object[]> interestRankings = keywordStatisticsRepository.findInterestRankingsAllTime();

        List<com.nova.nova_server.domain.trend.dto.SkillTrendResponse.RankingItem> rankingItems = new ArrayList<>();
        int rank = 1;

        for (Object[] row : interestRankings) {
            Long interestId = (Long) row[0];
            Long totalCount = (Long) row[1];

            // 해당 Interest 내에서 언급량 상위 4개 키워드 다시 조회
            // 해당 Interest 내에서 언급량 상위 4개 키워드 다시 조회 (전체 기간)
            List<Object[]> topKeywordsData = keywordStatisticsRepository.findTopKeywordsByInterestIdAllTime(
                    interestId, PageRequest.of(0, 4));


            List<com.nova.nova_server.domain.trend.dto.SkillTrendResponse.KeywordItem> keywordItems = topKeywordsData
                    .stream()
                    .map(k -> com.nova.nova_server.domain.trend.dto.SkillTrendResponse.KeywordItem
                            .builder()
                            .name((String) k[0])
                            .mentionCount((Long) k[1])
                            .build())
                    .collect(Collectors.toList());

            rankingItems.add(com.nova.nova_server.domain.trend.dto.SkillTrendResponse.RankingItem.builder()
                    .rank(rank++)
                    .interest(interestId)
                    .totalMentionCount(totalCount)
                    .keywords(keywordItems)
                    .build());
        }

        return com.nova.nova_server.domain.trend.dto.SkillTrendResponse.builder()
                .rankings(rankingItems)
                .build();
    }
}
