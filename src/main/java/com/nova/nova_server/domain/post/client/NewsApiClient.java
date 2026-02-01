package com.nova.nova_server.domain.post.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NewsApiClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.newsapi.base-url}")
    private String baseUrl;

    @Value("${external.newsapi.key}")
    private String apiKey;

    public String fetchRawJson() {
        return webClientBuilder.clone().build()
                .get()
                .uri(baseUrl + "/everything?q=(AI OR 인공지능 OR 개발자 OR software)&language=ko&sortBy=publishedAt")
                .header("X-Api-Key", apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}