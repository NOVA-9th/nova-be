package com.nova.nova_server.domain.post.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GNewsClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.gnews.base-url}")
    private String baseUrl;

    @Value("${external.gnews.key}")
    private String apiKey;

    public String fetchRawJson() {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/search?q=IT&token={token}&lang=ko", apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}