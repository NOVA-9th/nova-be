package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.CardType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsApiParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<Article> parse(String rawJson) {
        List<Article> articles = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode items = root.get("articles");

            if (items == null) {
                return articles;
            }

            for (JsonNode item : items) {
                String source = item.get("source").get("name").asText();
                String title = item.get("title").asText();
                String author = item.has("author") && !item.get("author").isNull()
                        ? item.get("author").asText()
                        : null;
                String description = item.has("description") && !item.get("description").isNull()
                        ? item.get("description").asText()
                        : null;
                String url = item.get("url").asText();

                LocalDateTime publishedAt = ZonedDateTime.parse(item.get("publishedAt").asText())
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime();

                articles.add(Article.builder()
                        .title(title)
                        .content(description)
                        .author(author)
                        .source(source)
                        .publishedAt(publishedAt)
                        .cardType(CardType.NEWS)
                        .url(url)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse NewsAPI response", e);
        }

        return articles;
    }
}