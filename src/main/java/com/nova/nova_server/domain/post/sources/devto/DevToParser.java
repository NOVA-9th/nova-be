package com.nova.nova_server.domain.post.sources.devto;

import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.sources.devto.dto.DevToArticle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DevToParser {
    public static Article toArticle(DevToArticle devToArticle, String markdownContent) {
        String title = firstNonEmpty(devToArticle.title(), "");
        String content = firstNonEmpty(markdownContent, devToArticle.description());
        String author = devToArticle.user() != null && devToArticle.user().name() != null
                ? devToArticle.user().name()
                : "";
        LocalDateTime publishedAt = parsePublishedAt(devToArticle.publishedAt());

        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .source("Dev.to")
                .publishedAt(publishedAt)
                .cardType(CardType.COMMUNITY)
                .url(devToArticle.url())
                .build();
    }

    private static String firstNonEmpty(String... values) {
        for (String v : values) {
            if (v != null && !v.isEmpty()) {
                return v;
            }
        }
        return "";
    }

    private static LocalDateTime parsePublishedAt(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
    }
}
