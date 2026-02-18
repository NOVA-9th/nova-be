package com.nova.nova_server.domain.batch.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeywordStatisticsTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        LocalDate targetDate = LocalDate.now().minusDays(1);
        LocalDateTime startDateTime = targetDate.atStartOfDay();
        LocalDateTime endDateTime = targetDate.plusDays(1).atStartOfDay();

        log.info("Starting Keyword Statistics Aggregation for date: {}", targetDate);

        String deleteSql = "DELETE FROM keyword_statistics WHERE stat_date = ?";
        int deletedCount = jdbcTemplate.update(deleteSql, Date.valueOf(targetDate));
        log.info("Deleted {} existing statistics records for date: {}", deletedCount, targetDate);

        // 통계 집계 및 저장
        String insertSql = """
                INSERT INTO keyword_statistics (keyword_id, stat_date, mention_count, created_at, updated_at)
                SELECT
                    keyword_id,
                    ?,
                    COUNT(*),
                    NOW(),
                    NOW()
                FROM card_news_keyword
                WHERE created_at >= ? AND created_at < ?
                GROUP BY keyword_id
                """;

        int insertedCount = jdbcTemplate.update(insertSql,
                Date.valueOf(targetDate),
                startDateTime,
                endDateTime);

        log.info("Inserted {} aggregated statistics records for date: {}", insertedCount, targetDate);

        return RepeatStatus.FINISHED;
    }
}
