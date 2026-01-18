package com.nova.nova_server.domain.post.service;

import com.nova.nova_server.domain.post.client.NewsApiClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.NewsApiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NewsAPI ingestion 파이프라인 orchestrator
 * Client에서 JSON을 가져오고 Parser로 Article로 변환한다.
 */
@Service
@RequiredArgsConstructor
public class NewsIngestionService {

    private final NewsApiClient client;
    private final NewsApiParser parser;

    public List<Article> fetchArticles() {
        try {
            String json = client.fetchRawJson();
            return parser.parse(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch articles from NewsAPI", e);
        }
    }
}
