package com.nova.nova_server.domain.post.sources.newsdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.model.SelfContainedArticleSource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NewsDataParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<ArticleSource> parse(String rawJson) {
        List<ArticleSource> articles = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode items = root.get("results");

            if (items == null) {
                return articles;
            }

            for (JsonNode item : items) {
                String title = item.get("title").asText();
                String description = item.has("description") && !item.get("description").isNull()
                        ? item.get("description").asText()
                        : null;
                String author = extractAuthor(item);
                String url = item.get("link").asText();
                String source = item.get("source_name").asText();
                String pubDate = item.get("pubDate").asText(); // UTC

                LocalDateTime publishedAt = LocalDateTime.parse(pubDate, FORMATTER);

                Article article = Article.builder()
                        .title(title)
                        .content(description)
                        .author(author)
                        .source(source)
                        .publishedAt(publishedAt)
                        .cardType(CardType.NEWS)
                        .url(url)
                        .build();
                articles.add(new SelfContainedArticleSource(url, article));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse NewsData response", e);
        }

        return articles;
    }

    private static String extractAuthor(JsonNode item) {
        JsonNode creatorNode = item.get("creator");
        if (creatorNode != null && creatorNode.isArray() && !creatorNode.isEmpty()) {
            return creatorNode.get(0).asText();
        }
        return null;
    }
}