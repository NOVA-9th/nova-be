package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.client.HackerNewsClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HackerNewsParser {

    private final HackerNewsClient client;

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

                // source 필드 하드코딩
                String source = "Hacker News";

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

                    // content가 비어있고 외부 URL이 있으면 크롤링 시도
                    if (content.isEmpty()) {
                        content = client.extractContent(url);
                    }
                } else {
                    // Ask HN, Show HN 등은 HN 링크로
                    url = "https://news.ycombinator.com/item?id=" + id;
                }

                // 크롤링 실패하거나 본문 없는 글은 스킵
                if (content.isEmpty() || "No Content".equals(content) || "Crawling Failed".equals(content)) {
                    continue;
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