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
public class JumpitClient {

    private final WebClient webClient;

    @Value("${external.jumpit.base-url}")
    private String baseUrl;

    @Value("${external.jumpit.user-agent}")
    private String userAgent;

    public List<JsonNode> fetchBackendPositions(int page) {
        return fetchPositionsByCategory(page, 2, 10); // 2: Server/Backend, Size: 10
    }

    public List<JsonNode> fetchFrontendPositions(int page) {
        return fetchPositionsByCategory(page, 1, 10); // 1: Frontend, Size: 10
    }

    private List<JsonNode> fetchPositionsByCategory(int page, int jobCategory, int size) {
        try {
            String url = String.format("%s/positions?sort=reg_dt&page=%d&jobCategory=%d&size=%d", baseUrl, page,
                    jobCategory, size);

            JsonNode response = webClient.get()
                    .uri(url)
                    .header("User-Agent", userAgent)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("result") && response.get("result").has("positions")) {
                List<JsonNode> positions = new ArrayList<>();
                response.get("result").get("positions").forEach(positions::add);
                return positions;
            }
        } catch (Exception e) {
            log.error("Jumpit API fetch 오류 (category: {}): {}", jobCategory, e.getMessage());
        }
        return List.of();
    }
}
