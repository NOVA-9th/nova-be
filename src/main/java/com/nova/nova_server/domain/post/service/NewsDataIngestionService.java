package com.nova.nova_server.domain.post.service;

import com.nova.nova_server.domain.post.client.NewsDataClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.NewsDataParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsDataIngestionService {

    private final NewsDataClient client;
    private final NewsDataParser parser;

    public List<Article> fetchArticles() {
        try {
            String json = client.fetchRawJson();
            return parser.parse(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch articles from NewsData.io", e);
        }
    }
}
