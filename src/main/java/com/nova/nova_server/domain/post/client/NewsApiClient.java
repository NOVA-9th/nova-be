package com.nova.nova_server.domain.post.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
//시간대 UTC
@Component
public class NewsApiClient {

    private final WebClient webClient;

    public NewsApiClient(
            @Value("${external.newsapi.base-url}") String baseUrl,
            @Value("${external.newsapi.key}") String apiKey
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Api-Key", apiKey)
                .build();
    }

    public String fetchRawJson() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/everything")
                        .queryParam("q", "(AI OR 인공지능 OR 개발자 OR software)")
                        .queryParam("language", "ko")
                        .queryParam("sortBy", "publishedAt")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
