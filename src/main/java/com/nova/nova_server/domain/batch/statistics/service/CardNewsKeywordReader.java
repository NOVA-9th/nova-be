package com.nova.nova_server.domain.batch.statistics.service;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CardNewsKeywordReader {

    private final EntityManagerFactory entityManagerFactory;

    public JpaCursorItemReader<CardNews> create(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        JpaCursorItemReader<CardNews> reader = new JpaCursorItemReader<>();
        reader.setName("keywordStatisticsReader");
        reader.setEntityManagerFactory(Objects.requireNonNull(entityManagerFactory));
        reader.setQueryString("""
                SELECT DISTINCT cn
                FROM CardNews cn
                LEFT JOIN FETCH cn.keywords
                WHERE cn.createdAt >= :startDateTime
                  AND cn.createdAt < :endDateTime
                ORDER BY cn.id ASC
                """);
        reader.setParameterValues(Objects.requireNonNull(Map.<String, Object>of(
                "startDateTime", startDateTime,
                "endDateTime", endDateTime
        )));
        return reader;
    }
}
