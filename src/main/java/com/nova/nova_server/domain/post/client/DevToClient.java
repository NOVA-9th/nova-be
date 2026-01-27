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
public class DevToClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.devto.base-url}")
    private String baseUrl;

    @Value("${external.devto.user-agent}")
    private String userAgent;

    public List<JsonNode> fetchArticlesWithContent(int limit) {
        List<JsonNode> detailedArticles = new ArrayList<>();

        try {
            // 인기글 목록 조회
            List<JsonNode> articles = webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/articles?top=1&per_page=" + limit)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToFlux(JsonNode.class)
                    .collectList()
                    .block();

            if (articles == null || articles.isEmpty()) {
                return detailedArticles;
            }

            // description말고 상세 본문 조회
            for (JsonNode article : articles) {
                if (article.has("id")) {
                    long id = article.get("id").asLong();
                    JsonNode detail = fetchArticleDetail(id);
                    if (detail != null) {
                        detailedArticles.add(detail);
                    }
                }
            }

        } catch (Exception e) {
            log.error("dev.to article fetch 오류", e);
        }

        return detailedArticles;
    }

    private JsonNode fetchArticleDetail(long id) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/articles/{id}", id)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (Exception e) {
            log.warn("dev.to article id: {}의 상세본문 불러오기 실패", id, e);
            return null;
        }
    }
}

