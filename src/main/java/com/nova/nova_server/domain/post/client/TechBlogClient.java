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

    private final WebClient.Builder webClientBuilder;

    @Value("${external.rss2json.base-url}")
    private String baseUrl;

    public JsonNode fetchRssAsJson(String rssUrl) {
        try {
            // rss2json API 호출 URI 생성
            String uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("rss_url", rssUrl)
                    .build()
                    .toUriString();

            return webClientBuilder
                    .exchangeStrategies(builder ->
                            builder.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))) // 크기 증가(길이 긴 본문 풀로 받아오기위해)
                    .build()
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