package com.nova.nova_server.domain.post.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
//시간대 UTC
@Component
public class NewsDataClient {

    private final WebClient webClient;

    public NewsDataClient(
            @Value("${external.newsdata.base-url}") String baseUrl,
            @Value("${external.newsdata.key}") String apiKey
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
    }

    private final String apiKey;

    public String fetchRawJson() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/news")
                        .queryParam("q", "(AI OR 인공지능 OR 개발자 OR software)")
                        .queryParam("language", "ko")
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
