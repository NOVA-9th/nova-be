package com.nova.nova_server.domain.batch.statistics.service;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.entity.CardNewsKeyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeywordStatisticsWriter implements ItemWriter<CardNews> {
    private static final ZoneId KST_ZONE = ZoneId.of("Asia/Seoul");


    private static final String UPSERT_SQL = """
            INSERT INTO keyword_statistics (keyword_id, stat_date, mention_count, created_at, updated_at)
            VALUES (:keywordId, :statDate, :count, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
                mention_count = mention_count + VALUES(mention_count),
                updated_at = NOW()
            """;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void write(@NonNull Chunk<? extends CardNews> chunk) {
        if (chunk.isEmpty()) {
            return;
        }

        Map<KeywordStatKey, Integer> countByKeywordAndDate = new HashMap<>();
        for (CardNews cardNews : chunk) {
            if (cardNews.getPublishedAt() == null || cardNews.getKeywords() == null) {
                continue;
            }

            // publishedAt is stored as UTC; convert to KST date for daily stats.
            LocalDate statDate = cardNews.getPublishedAt()
                    .atOffset(ZoneOffset.UTC)
                    .atZoneSameInstant(KST_ZONE)
                    .toLocalDate();
            for (CardNewsKeyword cardNewsKeyword : cardNews.getKeywords()) {
                Long keywordId = cardNewsKeyword.getKeywordId();
                if (keywordId == null) {
                    continue;
                }
                KeywordStatKey key = new KeywordStatKey(keywordId, statDate);
                int currentCount = countByKeywordAndDate.getOrDefault(key, 0);
                countByKeywordAndDate.put(key, currentCount + 1);
            }
        }

        int upsertedCount = 0;
        for (Map.Entry<KeywordStatKey, Integer> entry : countByKeywordAndDate.entrySet()) {
            KeywordStatKey key = entry.getKey();
            Integer count = entry.getValue();

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("keywordId", key.keywordId())
                    .addValue("statDate", key.statDate())
                    .addValue("count", count);

            namedParameterJdbcTemplate.update(UPSERT_SQL, params);
            upsertedCount++;
        }

        log.info(
                "Keyword statistics incremented. cardNewsCount={}, keywordStatsUpdated={}",
                chunk.size(),
                upsertedCount
        );
    }

    private record KeywordStatKey(Long keywordId, LocalDate statDate) {}
}
