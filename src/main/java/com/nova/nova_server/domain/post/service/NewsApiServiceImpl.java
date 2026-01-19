package com.nova.nova_server.domain.post.service;

import com.nova.nova_server.domain.post.client.NewsApiClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.NewsApiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsApiServiceImpl implements NewsApiService {

    private final NewsApiClient client;
    private final NewsApiParser parser;

    @Override
    public List<Article> fetchArticles() {
        try {
            String json = client.fetchRawJson();
            return parser.parse(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch articles from NewsAPI", e);
        }
    }
}
