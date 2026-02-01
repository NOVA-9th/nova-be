package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverNewsApiClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.navernewsapi.base-url}")
    private String baseUrl;

    @Value("${external.navernewsapi.client-id}")
    private String clientId;

    @Value("${external.navernewsapi.client-secret}")
    private String clientSecret;

    public List<JsonNode> fetchAll() {
        List<JsonNode> results = new ArrayList<>();

        // 여러 키워드로 검색
        String[] keywords = {
                "인공지능",
                "개발자 채용",
                "클라우드",
                "빅데이터",
                "스타트업 투자"
        };

        for (String keyword : keywords) {
            try {
                JsonNode result = webClientBuilder.clone().build()
                        .get()
                        .uri(baseUrl + "/news.json?query={query}&display=10&sort=date", keyword)
                        .header("X-Naver-Client-Id", clientId)
                        .header("X-Naver-Client-Secret", clientSecret)
                        .header("Content-Type", "application/json")
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();

                if (result != null) {
                    results.add(result);
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch articles for keyword: " + keyword + " - " + e.getMessage());
            }
        }

        return results;
    }
}