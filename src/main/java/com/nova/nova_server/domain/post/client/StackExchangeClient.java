package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StackExchangeClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.stackexchange.base-url}")
    private String baseUrl;

    @Value("${external.stackexchange.user-agent}")
    private String userAgent;

    public List<JsonNode> fetchQuestions(String tags, String sort, int limit) {
        try {
            WebClient client = webClientBuilder.clone()
                    .baseUrl(baseUrl)
                    .defaultHeader("User-Agent", userAgent)
                    .build();

            JsonNode response = client.get()
                    .uri(uriBuilder -> {
                        uriBuilder
                                .path("/questions")
                                .queryParam("order", "desc")
                                .queryParam("sort", sort)
                                .queryParam("site", "stackoverflow")
                                .queryParam("pagesize", limit)
                                .queryParam("filter", "!T*hPNRA69ofM1izkPP"); // stackoverflow 커스텀필터(질문+본문+답변포함)

                        if (tags != null && !tags.isEmpty()) {
                            uriBuilder.queryParam("tagged", tags);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("items")) {
                List<JsonNode> items = new ArrayList<>();
                response.get("items").forEach(items::add);
                return items;
            }
        } catch (Exception e) {
            log.error("StackExchange 질문 가져오기 실패. tags: {}, sort: {}", tags, sort, e);
        }
        return new ArrayList<>();
    }
}
