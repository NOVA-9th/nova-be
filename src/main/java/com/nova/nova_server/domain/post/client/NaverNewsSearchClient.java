package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NaverNewsSearchClient {

    private final WebClient webClient;

    @Value("${external.navernewssearch.base-url}")
    private String baseUrl;

    @Value("${external.navernewssearch.client-id}")
    private String clientId;

    @Value("${external.navernewssearch.client-secret}")
    private String clientSecret;

    public JsonNode fetch() {
        return webClient.get()
                .uri(baseUrl + "/news.json?query=IT&display=10&sort=date")
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}