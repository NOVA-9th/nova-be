package com.nova.nova_server.domain.ingestion.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.ingestion.model.Article;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

            String content = item.get("content").asText(null);
            String description = item.get("description").asText(null);
            String body = content != null ? content : description;

            String url = item.get("url").asText();

            LocalDateTime publishedAt = ZonedDateTime
                    .parse(item.get("publishedAt").asText())
                    .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();

            result.add(new Article(
                    title,
                    body,
                    author,
                    source,
                    publishedAt,
                    url
            ));
        }

        return result;
    }
}
