package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.CardType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class DevToParser {

    public List<Article> parse(List<JsonNode> items) {
        List<Article> articles = new ArrayList<>();

        for (JsonNode item : items) {
            try {
                String title = item.has("title") ? item.get("title").asText() : "";

                // 본문 가져오고 없으면 description 가져오기
                String content = "";
                if (item.has("body_markdown") && !item.get("body_markdown").isNull()) {
                    content = item.get("body_markdown").asText();
                } else if (item.has("description") && !item.get("description").isNull()) {
                    content = item.get("description").asText();
                }

                String author = "";
                if (item.has("user") && item.get("user").has("name")) {
                    author = item.get("user").get("name").asText();
                }

                String url = item.has("url") ? item.get("url").asText() : null;

                LocalDateTime publishedAt = null;
                if (item.has("published_at")) {
                    String dateStr = item.get("published_at").asText();
                    if (dateStr != null && !dateStr.isEmpty()) {
                        publishedAt = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
                    }
                }

                articles.add(Article.builder()
                        .title(title)
                        .content(content)
                        .author(author)
                        .source("Dev.to")
                        .publishedAt(publishedAt)
                        .cardType(CardType.COMMUNITY)
                        .url(url)
                        .build());

            } catch (Exception e) {
                System.err.println("dev.to 파싱오류: " + e.getMessage());
            }
        }

        return articles;
    }
}

