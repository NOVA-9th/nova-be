package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
//시간대 KST
@Component
public class NaverNewsClient {

    private final WebClient webClient;

    public NaverNewsClient(@Qualifier("naverNewsWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public JsonNode fetch(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/news.json")
                        .queryParam("query", query)
                        .queryParam("display", 50)
                        .queryParam("sort", "date")
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
