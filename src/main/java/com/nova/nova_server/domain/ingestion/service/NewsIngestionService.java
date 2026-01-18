package com.nova.nova_server.domain.ingestion.service;

import com.nova.nova_server.domain.ingestion.client.NewsApiClient;
import com.nova.nova_server.domain.ingestion.model.Article;
import com.nova.nova_server.domain.ingestion.parser.NewsApiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
