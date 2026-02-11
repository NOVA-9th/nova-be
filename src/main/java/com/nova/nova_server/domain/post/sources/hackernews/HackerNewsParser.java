package com.nova.nova_server.domain.post.sources.hackernews;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class HackerNewsParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * JsonNode를 DTO로 파싱. story/job만 반환하며 그 외는 null.
     */
    public static HackerNewsItem parse(JsonNode item) {
        if (item == null || !item.has("type")) {
            return null;
        }
        try {
            HackerNewsItem dto = objectMapper.treeToValue(item, HackerNewsItem.class);
            if (dto == null || dto.type() == null) {
                return null;
            }
            if (!"story".equals(dto.type()) && !"job".equals(dto.type())) {
                return null;
            }
            return dto;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * DTO를 Article로 변환. "No Content" / "Crawling Failed"인 경우 null.
     */
    public static Article toArticle(HackerNewsItem dto, String content) {
        if (dto == null) {
            return null;
        }
        String url = resolveUrl(dto);
        LocalDateTime publishedAt = dto.time() != null
                ? LocalDateTime.ofInstant(Instant.ofEpochSecond(dto.time()), ZoneOffset.UTC)
                : null;

        return Article.builder()
                .title(dto.title())
                .content(content)
                .author(dto.by())
                .source("Hacker News")
                .publishedAt(publishedAt)
                .cardType(CardType.COMMUNITY)
                .url(url)
                .build();
    }

    private static String resolveUrl(HackerNewsItem dto) {
        if (dto.url() != null && !dto.url().isEmpty()) {
            return dto.url();
        }
        int id = dto.id() != null ? dto.id() : 0;
        return "https://news.ycombinator.com/item?id=" + id;
    }

    /**
     * HTML 엔티티 디코딩 + HTML 태그 제거
     */
    public static String cleanHtml(String input) {
        if (input == null) return "";

        String decoded = StringEscapeUtils.unescapeHtml4(input);
        String cleaned = decoded.replaceAll("<[^>]+>", "");
        return cleaned.replaceAll("\\s+", " ").trim();
    }
}
