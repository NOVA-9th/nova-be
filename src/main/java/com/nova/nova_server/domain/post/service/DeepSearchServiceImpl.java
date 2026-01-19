package com.nova.nova_server.domain.post.service;

import com.nova.nova_server.domain.post.client.DeepSearchClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.DeepSearchParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeepSearchServiceImpl implements DeepSearchService {

    private final DeepSearchClient client;
    private final DeepSearchParser parser;

    @Override
    public List<Article> fetchArticles() {
        try {
            String json = client.fetchRawJson();
            return parser.parse(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch articles from DeepSearch", e);
        }
    }
}

