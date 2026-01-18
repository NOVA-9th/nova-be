package com.nova.nova_server.domain.post.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
//시간대 UTC
@Component
public class NewsDataClient {

    private final WebClient webClient;
    private final String apiKey;

    public NewsDataClient(
            @Qualifier("newsDataWebClient") WebClient webClient,
            @Value("${external.newsdata.key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

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
