package com.nova.nova_server.domain.post.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
//시간대 KST
//딥서치 뉴스 특성상 연예뉴스가 상위권에 먼저 나오는 편
@Component
public class DeepSearchClient {

    private final WebClient webClient;
    private final String apiKey;

    public DeepSearchClient(
            @Qualifier("deepSearchWebClient") WebClient webClient,
            @Value("${external.deepsearch.key}") String apiKey
    ) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    public String fetchRawJson() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/articles")
                        .queryParam("keyword", "AI OR 인공지능 OR 블록체인 OR 개발자")
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
