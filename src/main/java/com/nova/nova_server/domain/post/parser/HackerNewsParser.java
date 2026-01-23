package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.CardType;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class HackerNewsParser {

    public List<Article> parse(List<JsonNode> items) {
        List<Article> articles = new ArrayList<>();

        for (JsonNode item : items) {
            try {
                if (!item.has("type")) {
                    continue;
                }

                String type = item.get("type").asText();

                // story와 job만 처리
                if (!"story".equals(type) && !"job".equals(type)) {
                    continue;
                }

                String title = item.has("title") ? item.get("title").asText() : null;
                String author = item.has("by") ? item.get("by").asText() : null;

                // source 필드 확인 (있으면 가져오고 없으면 null)
                String source = item.has("source") && !item.get("source").isNull()
                        ? item.get("source").asText()
                        : null;

                // text 필드에서 content 추출
                String content = "";
                if (item.has("text") && !item.get("text").isNull()) {
                    content = cleanHtml(item.get("text").asText());
                }

                // URL 추출
                String url;
                int id = item.has("id") ? item.get("id").asInt() : 0;
                if (item.has("url") && !item.get("url").asText().isEmpty()) {
                    url = item.get("url").asText();
                } else {
                    // Ask HN, Show HN 등은 HN 링크로
                    url = "https://news.ycombinator.com/item?id=" + id;
                }

                // Unix timestamp를 LocalDateTime으로 변환
                long timestamp = item.has("time") ? item.get("time").asLong() : 0;
                LocalDateTime publishedAt = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(timestamp),
                        ZoneOffset.UTC
                );

                articles.add(Article.builder()
                        .title(title)
                        .content(content)
                        .author(author)
                        .source(source)
                        .publishedAt(publishedAt)
                        .cardType(CardType.NEWS)
                        .url(url)
                        .build());

            } catch (Exception e) {
                System.err.println("Failed to parse HackerNews item: " + e.getMessage());
            }
        }

        return articles;
    }

    /**
     * HTML 엔티티 디코딩 + HTML 태그 제거
     */
    private String cleanHtml(String input) {
        if (input == null) return null;

        // 1. HTML 엔티티 디코딩 (&#x2F; → /, &#x27; → ')
        String decoded = StringEscapeUtils.unescapeHtml4(input);

        // 2. HTML 태그 제거 (<p>, <pre>, <code> 등)
        String cleaned = decoded.replaceAll("<[^>]+>", "");

        // 3. 연속된 공백을 하나로
        return cleaned.replaceAll("\\s+", " ").trim();
    }
}