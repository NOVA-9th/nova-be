package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.CardType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class DeepSearchParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<Article> parse(String rawJson) {
        List<Article> articles = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode items = root.get("data");

            if (items == null) {
                return articles;
            }

            for (JsonNode item : items) {
                String title = item.get("title").asText();
                String description = item.has("summary") && !item.get("summary").isNull()
                        ? item.get("summary").asText()
                        : null;
                String author = item.has("author") && !item.get("author").isNull()
                        ? item.get("author").asText()
                        : null;
                String source = item.get("publisher").asText();
                String url = item.has("content_url") && !item.get("content_url").isNull()
                        ? item.get("content_url").asText()
                        : null;
                String ts = item.get("published_at").asText(); // UTC

                LocalDateTime publishedAt = LocalDateTime.parse(ts, FORMATTER);

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
            throw new RuntimeException("Failed to parse DeepSearch response", e);
        }

        return articles;
    }
}