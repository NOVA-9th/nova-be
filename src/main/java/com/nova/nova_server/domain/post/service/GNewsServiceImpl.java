package com.nova.nova_server.domain.post.service;

import com.nova.nova_server.domain.post.client.GNewsClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.GNewsParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GNewsServiceImpl implements ArticleApiService {

    private final GNewsClient client;
    private final GNewsParser parser;

    @Override
    public List<Article> fetchArticles() {
        try {
            String json = client.fetchRawJson();
            return parser.parse(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch articles from GNews", e);
        }
    }

    @Override
    public String getProviderName() {
        return "GNews";
    }
}