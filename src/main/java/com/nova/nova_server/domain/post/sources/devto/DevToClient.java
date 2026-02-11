package com.nova.nova_server.domain.post.sources.devto;

import com.nova.nova_server.domain.post.HtmlCleaner;
import com.nova.nova_server.domain.post.sources.devto.dto.DevToArticle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DevToClient {

    private final WebClient webClient;

    @Value("${external.devto.base-url}")
    private String baseUrl;

    @Value("${external.devto.user-agent}")
    private String userAgent;

    @Value("${external.devto.perPage:1000}")
    private int perPage;

    public List<DevToArticle> fetchArticles() {
        List<DevToArticle> articles = webClient.get()
                .uri(baseUrl + "/articles?top=1&per_page=" + perPage)
                .header("User-Agent", userAgent)
                .retrieve()
                .bodyToFlux(DevToArticle.class)
                .collectList()
                .block();

        if (articles != null && !articles.isEmpty()) {
            return articles;
        }
        else {
            return Collections.emptyList();
        }
    }

    public String fetchArticleDetail(String url) {
        String html = webClient.get()
                .uri(url)
                .header("User-Agent", userAgent)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return HtmlCleaner.getTextFromHtml(html);
    }
}
