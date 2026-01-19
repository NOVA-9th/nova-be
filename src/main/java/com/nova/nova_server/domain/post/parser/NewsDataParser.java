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
public class NewsDataParser {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Article> parse(String rawJson) throws Exception {
        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode items = root.get("results");

        List<Article> result = new ArrayList<>();

        for (JsonNode item : items) {

            String title = item.get("title").asText();
            String description = item.get("description").asText(null);
            String author = extractAuthor(item);
            String url = item.get("link").asText();
            String source = item.get("source_name").asText();

            String pubDate = item.get("pubDate").asText(); // UTC 문자열

            LocalDateTime publishedAt = LocalDateTime.parse(pubDate, formatter);

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

    private String extractAuthor(JsonNode item) {
        JsonNode creatorNode = item.get("creator");
        if (creatorNode != null && creatorNode.isArray() && creatorNode.size() > 0) {
            return creatorNode.get(0).asText();
        }
        return null;
    }
}
