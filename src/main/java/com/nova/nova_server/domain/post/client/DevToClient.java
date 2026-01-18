package com.nova.nova_server.domain.post.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DevToClient {

    private final WebClient webClient;

    public DevToClient(@Qualifier("devToWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public String fetchRawJson() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/articles")
//                        .queryParam("state", "rising") // 인기 급상승 우선 가져오는 코드 논의 후 나중에 필요하면 추가
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
