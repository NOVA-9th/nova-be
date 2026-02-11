package com.nova.nova_server.domain.post.sources.newsdata;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NewsDataClient {

    private final WebClient webClient;

    @Value("${external.newsdata.base-url}")
    private String baseUrl;

    @Value("${external.newsdata.key}")
    private String apiKey;

    public String fetchRawJson() {
        return webClient.get()
                .uri(baseUrl + "/news?apikey={apiKey}&q=IT&language=ko", apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}