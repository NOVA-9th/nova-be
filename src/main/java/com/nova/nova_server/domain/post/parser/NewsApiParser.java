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

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Article> parse(String rawJson) throws Exception {
        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode items = root.get("articles");

        List<Article> result = new ArrayList<>();

        for (JsonNode item : items) {

            String source = item.get("source").get("name").asText();
            String title = item.get("title").asText();
            String author = item.get("author").asText(null);
            String description = item.get("description").asText(null);
            String url = item.get("url").asText();

            LocalDateTime publishedAt =
                    ZonedDateTime.parse(item.get("publishedAt").asText())
                            .withZoneSameInstant(ZoneOffset.UTC)
                            .toLocalDateTime();

            result.add(new Article(
                    title,
                    description,
                    author,
                    source,
                    publishedAt,
                    CardType.NEWS,
                    url
            ));
        }

        return result;
    }
}
