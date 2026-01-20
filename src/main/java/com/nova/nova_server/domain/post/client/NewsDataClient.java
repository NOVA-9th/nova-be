package com.nova.nova_server.domain.post.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NewsDataClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.newsdata.base-url}")
    private String baseUrl;

    @Value("${external.newsdata.key}")
    private String apiKey;

    public String fetchRawJson() {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/news?q=(AI OR 인공지능 OR 개발자 OR software)&language=ko&apikey={apiKey}", apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}