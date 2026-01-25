package com.nova.nova_server.domain.post.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.client.HackerNewsClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.HackerNewsParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HackerNewsServiceImpl implements ArticleApiService {

    private final HackerNewsClient client;
    private final HackerNewsParser parser;

    @Override
    public List<Article> fetchArticles() {
        List<JsonNode> items = client.fetchMixedStories();
        return parser.parse(items);
    }

    @Override
    public String getProviderName() {
        return "HackerNews";
    }
}