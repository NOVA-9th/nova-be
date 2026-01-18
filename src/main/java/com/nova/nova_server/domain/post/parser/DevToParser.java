package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.post.model.Article;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DevToParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Article> parse(String rawJson) throws Exception {
        JsonNode items = objectMapper.readTree(rawJson);
        List<Article> result = new ArrayList<>();

        for (JsonNode item : items) {
            String title = item.get("title").asText();
            String description = item.has("description") ? item.get("description").asText() : "";

            String author = "Unknown";
            if (item.has("user") && item.get("user").has("name")) {
                author = item.get("user").get("name").asText();
            }

            String url = item.get("url").asText();
            String source = "Dev.to";

            String publishedAtStr = item.get("published_at").asText();

            LocalDateTime publishedAt = ZonedDateTime // NewsApiParser 구조에 맞춰 UTC를 asia로 수정하였음
                    .parse(publishedAtStr)
                    .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();

            result.add(new Article(
                    title,
                    description,
                    author,
                    source,
                    publishedAt,
                    url
            ));
        }

        return result;
    }
}