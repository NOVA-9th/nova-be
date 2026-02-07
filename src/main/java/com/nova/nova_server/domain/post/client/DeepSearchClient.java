package com.nova.nova_server.domain.post.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class DeepSearchClient {

    // private final WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    @Value("${external.deepsearch.base-url}")
    private String baseUrl;

    @Value("${external.deepsearch.key}")
    private String apiKey;

    public String fetchRawJson() {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/articles?keyword=(개발자)&api_key={apiKey}", apiKey)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}