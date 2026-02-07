package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class TechBlogClient {

    private final WebClient webClient;

    @Value("${external.rss2json.base-url}")
    private String baseUrl;

    public JsonNode fetchRssAsJson(String rssUrl) {
        try {
            // rss2json API 호출 URI 생성
            String uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("rss_url", rssUrl)
                    .build()
                    .toUriString();

            return webClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

        } catch (Exception e) {
            log.warn("RSS Fetch 실패 (URL: {}): {}", rssUrl, e.getMessage());
            return null;
        }
    }
}