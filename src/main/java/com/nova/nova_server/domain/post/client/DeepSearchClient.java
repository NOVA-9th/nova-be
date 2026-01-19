package com.nova.nova_server.domain.post.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
//시간대 KST -> 타임존 없어서 UTC로 가정
@Component
public class DeepSearchClient {

    private final WebClient webClient;
    private final String apiKey;

    public DeepSearchClient(
            @Value("${external.deepsearch.base-url}") String baseUrl,
            @Value("${external.deepsearch.key}") String apiKey
    ) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public String fetchRawJson() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/articles")
                        .query("keyword=(AI OR 인공지능 OR 블록체인 OR 개발자)&api_key=" + apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
