package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
//시간대 KST->UTC 수정
@Component
public class NaverNewsSearchClient {

    private final WebClient webClient;

    public NaverNewsSearchClient(
            @Value("${external.navernews.base-url}") String baseUrl,
            @Value("${external.navernews.client-id}") String clientId,
            @Value("${external.navernews.client-secret}") String clientSecret
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

    public JsonNode fetch(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/news.json")
                        .queryParam("query", query)
                        .queryParam("display", 100)
                        .queryParam("sort", "date")
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
