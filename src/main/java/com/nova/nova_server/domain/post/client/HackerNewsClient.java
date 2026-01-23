package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
//해커뉴스 api source 비어있음
@Component
@RequiredArgsConstructor
public class HackerNewsClient {

    private final WebClient.Builder webClientBuilder;

    private static final String BASE_URL = "https://hacker-news.firebaseio.com/v0";

    public List<JsonNode> fetchMixedStories() {
        List<JsonNode> allItems = new ArrayList<>();

        // 1. Top Stories (일반 인기 글) - content 비어있고, 외부 링크만 던져줌
        allItems.addAll(fetchStoriesByType("/topstories.json", 10));

        // 2. Show HN (프로젝트)
        allItems.addAll(fetchStoriesByType("/showstories.json", 5));

        // 3. Ask HN (질문)
        allItems.addAll(fetchStoriesByType("/askstories.json", 3));

        // 4. Job (채용)
        allItems.addAll(fetchStoriesByType("/jobstories.json", 2));

        return allItems;
    }

    private List<JsonNode> fetchStoriesByType(String endpoint, int limit) {
        List<JsonNode> items = new ArrayList<>();

        try {
            List<Integer> storyIds = webClientBuilder.build()
                    .get()
                    .uri(BASE_URL + endpoint)
                    .retrieve()
                    .bodyToFlux(Integer.class)
                    .collectList()
                    .block();

            if (storyIds == null || storyIds.isEmpty()) {
                return items;
            }

            List<Integer> topN = storyIds.subList(0, Math.min(limit, storyIds.size()));

            for (Integer id : topN) {
                try {
                    JsonNode item = webClientBuilder.build()
                            .get()
                            .uri(BASE_URL + "/item/{id}.json", id)
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .block();

                    if (item != null) {
                        items.add(item);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to fetch item: " + id);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch stories from: " + endpoint);
        }

        return items;
    }
}