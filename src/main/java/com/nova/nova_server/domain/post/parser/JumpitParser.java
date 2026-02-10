package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.post.model.Article;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class JumpitParser {

    public List<Article> parse(List<JsonNode> items) {
        List<Article> articles = new ArrayList<>();

        for (JsonNode item : items) {
            try {
                String title = item.has("title") ? item.get("title").asText() : "";
                String company = item.has("companyName") ? item.get("companyName").asText() : "";

                // 기술 스택 및 위치 정보를 조합하여 내용 생성
                StringBuilder contentBuilder = new StringBuilder();
                if (item.has("techStacks")) {
                    String techStacks = StreamSupport.stream(item.get("techStacks").spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.joining(", "));
                    contentBuilder.append("기술 스택: ").append(techStacks).append("\n");
                }

                if (item.has("locations")) {
                    String locations = StreamSupport.stream(item.get("locations").spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.joining(", "));
                    contentBuilder.append("위치: ").append(locations);
                }

                String id = item.has("id") ? item.get("id").asText() : "";
                String url = id.isEmpty() ? null : "https://www.jumpit.co.kr/position/" + id;

                articles.add(Article.builder()
                        .title(title)
                        .content(contentBuilder.toString().trim())
                        .author(company)
                        .source("Jumpit")
                        .publishedAt(LocalDateTime.now(ZoneOffset.UTC))
                        .cardType(CardType.JOB)
                        .url(url)
                        .build());

            } catch (Exception e) {
                System.err.println("Jumpit 파싱 오류: " + e.getMessage());
            }
        }

        return articles;
    }
}
