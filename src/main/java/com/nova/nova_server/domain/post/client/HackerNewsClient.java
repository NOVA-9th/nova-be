package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HackerNewsClient {

    private final WebClient webClient;

    @Value("${external.hackernews.base-url}")
    private String baseUrl;

    public List<JsonNode> fetchMixedStories() {
        List<JsonNode> allItems = new ArrayList<>();

        // 1. Top Stories (일반 인기 글)
        allItems.addAll(fetchStoriesByType("/topstories.json", 20));

        // 2. Show HN (프로젝트)
        allItems.addAll(fetchStoriesByType("/showstories.json", 5));

        // 3. Ask HN (질문)
        allItems.addAll(fetchStoriesByType("/askstories.json", 5));

        return allItems;
    }

    private List<JsonNode> fetchStoriesByType(String endpoint, int limit) {
        List<JsonNode> items = new ArrayList<>();

        try {
            List<Integer> storyIds = webClient
                    .get()
                    .uri(baseUrl + endpoint)
                    .retrieve()
                    .bodyToFlux(Integer.class)
                    .collectList()
                    .block();

            if (storyIds == null || storyIds.isEmpty()) {
                log.warn("No HN story IDs returned from: {}", endpoint);
                return items;
            }

            List<Integer> topN = storyIds.subList(0, Math.min(limit, storyIds.size()));

            for (Integer id : topN) {
                try {
                    JsonNode item = webClient
                            .get()
                            .uri(baseUrl + "/item/{id}.json", id)
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .block();

                    if (item != null) {
                        items.add(item);
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch HN item: {}", id);
                }
            }
        } catch (

        Exception e) {
            log.error("Failed to fetch HN stories from: {}", endpoint, e);
        }

        return items;
    }

    /**
     * 외부 URL에서 본문 콘텐츠를 크롤링
     */
    public String extractContent(String url) {
        // 해커뉴스 내부 링크는 크롤링 불필요 (text 필드에 이미 내용 있음)
        if (url == null || url.isEmpty() || url.contains("news.ycombinator.com")) {
            return "";
        }

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            String content = doc.select("article").text();
            if (content.isEmpty()) {
                content = doc.select("p").text();
            }

            // <article>태그가 없거나, <p> 태그가 있어도 본문이 아닌 다른 내용이거나, Selenium 사용해야하는 동적으로 렌더링되는 사이트
            if (content.isEmpty()) {
                return "No Content";// 크롤링 성공했으나 본문 없음
            }

            return content.length() > 500 ? content.substring(0, 500) + "..." : content;

        } catch (Exception e) {
            log.warn("Failed to extract HN content from: {}", url);
            return "Crawling Failed";// 크롤링 실패
        }
    }
}