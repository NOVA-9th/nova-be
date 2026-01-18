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

/**
 * NewsAPI JSON → Article 도메인 모델로 변환하는 파서
 */
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

            // description만 사용 (NewsAPI 정책상 요약본 확보 용)
            String description = item.get("description").asText(null);
            String body = description;

            String url = item.get("url").asText();

            LocalDateTime publishedAt = ZonedDateTime//시간 UTC라 asia로 임의 수정
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
