package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.CardType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class GNewsParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    public List<Article> parse(String json) {
        List<Article> articles = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(json);

            if (!root.has("articles")) {
                return articles;
            }

            JsonNode articlesNode = root.get("articles");

            for (JsonNode item : articlesNode) {
                String title = item.get("title").asText();
                String description = item.has("description") && !item.get("description").isNull()
                        ? item.get("description").asText()
                        : "";
                String content = item.has("content") && !item.get("content").isNull()
                        ? item.get("content").asText()
                        : description;
                String url = item.get("url").asText();
                String publishedAt = item.get("publishedAt").asText();

                JsonNode sourceNode = item.get("source");
                String sourceName = sourceNode.get("name").asText();

                LocalDateTime publishedDateTime = ZonedDateTime.parse(publishedAt, ISO_FORMATTER)
                        .toLocalDateTime();

                articles.add(Article.builder()
                        .title(title)
                        .content(content)
                        .author(null) // GNews는 author 정보 없음
                        .source(sourceName)
                        .publishedAt(publishedDateTime)
                        .cardType(CardType.NEWS)
                        .url(url)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GNews response", e);
        }

        return articles;
    }
}