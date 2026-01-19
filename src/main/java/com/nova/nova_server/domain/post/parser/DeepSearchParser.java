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

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<Article> parse(String rawJson) throws Exception {

        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode items = root.get("data");

        List<Article> result = new ArrayList<>();

        for (JsonNode item : items) {

            String title = item.get("title").asText();
            String description = item.get("summary").asText(null);
            String author = item.get("author").asText(null);
            String source = item.get("publisher").asText();
            String url = item.get("content_url").asText(null);

            String ts = item.get("published_at").asText(); // e.g. 2026-01-18T21:30:49 (UTC)

            LocalDateTime publishedAt = LocalDateTime.parse(ts, formatter);

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
