package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NaverNewsSearchClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.navernews.base-url}")
    private String baseUrl;

    @Value("${external.navernews.client-id}")
    private String clientId;

    @Value("${external.navernews.client-secret}")
    private String clientSecret;

    public JsonNode fetch(String query) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/news.json?query={query}&display=100&sort=date", query)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}