package com.nova.nova_server.domain.post.sources.newsapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.model.SelfContainedArticleSource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewsApiParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<ArticleSource> parse(String rawJson) {
        List<ArticleSource> articles = new ArrayList<>();

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
            throw new RuntimeException("Failed to parse NewsAPI response", e);
        }

        return articles;
    }
}