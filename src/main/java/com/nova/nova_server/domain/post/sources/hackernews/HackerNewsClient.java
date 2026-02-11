package com.nova.nova_server.domain.post.sources.hackernews;

import com.nova.nova_server.domain.post.HtmlCleaner;
import com.nova.nova_server.domain.post.model.ArticleSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${external.hackernews.user-agent}")
    private String userAgent;

    public List<ArticleSource> fetchMixedStories() {
        List<ArticleSource> allItems = new ArrayList<>();

        // 1. Top Stories (일반 인기 글)
        allItems.addAll(fetchStoriesByType("/topstories.json"));

        // 2. Show HN (프로젝트)
        allItems.addAll(fetchStoriesByType("/showstories.json"));

        // 3. Ask HN (질문)
        allItems.addAll(fetchStoriesByType("/askstories.json"));

        return allItems;
    }

    public String getArticleUrl(int id) {
        return baseUrl + "/item/" + id + ".json";
    }

    private List<ArticleSource> fetchStoriesByType(String endpoint) {
        return webClient
                .get()
                .uri(baseUrl + endpoint)
                .retrieve()
                .bodyToFlux(Integer.class)
                .collectList()
                .block()
                .stream()
                .map(id -> (ArticleSource) new HackerNewsArticleSource(id, this))
                .toList();
    }

    public HackerNewsItem fetchItem(int id) {
        return webClient.get()
                .uri(baseUrl + "/item/{id}.json", id)
                .retrieve()
                .bodyToMono(HackerNewsItem.class)
                .block();
    }

    /**
     * 외부 URL에서 본문 콘텐츠를 크롤링
     */
    public String fetchContent(HackerNewsItem item) {
        // 해커뉴스 내부 링크는 크롤링 불필요 (text 필드에 이미 내용 있음)
        String url = item.url();
        if (url == null || url.isEmpty() || url.contains("news.ycombinator.com")) {
            String text = item.text();
            if (text != null && !text.isBlank()) {
                return cleanHtml(text);
            }
            log.warn("text is empty {}", url);
        }

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .timeout(5000)
                    .get();

            String content = doc.select("article").text();
            if (content.isBlank()) {
                content = HtmlCleaner.getTextFromHtml(doc.outerHtml());
            }

            if (content.isBlank()) {
                log.warn("content is empty {}", url);
            }

            return content;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String cleanHtml(String input) {
        if (input == null) return null;

        // 1. HTML 엔티티 디코딩 (&#x2F; → /, &#x27; → ')
        String decoded = StringEscapeUtils.unescapeHtml4(input);

        // 2. HTML 태그 제거 (<p>, <pre>, <code> 등)
        String cleaned = decoded.replaceAll("<[^>]+>", "");

        // 3. 연속된 공백을 하나로
        return cleaned.replaceAll("\\s+", " ").trim();
    }
}