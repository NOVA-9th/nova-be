package com.nova.nova_server.domain.post.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.client.DevToClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.DevToParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DevToServiceImpl implements ArticleApiService {

    private final DevToClient client;
    private final DevToParser parser;

    @Override
    public List<Article> fetchArticles() {
        // limit = 가져오는 기사 개수
        List<JsonNode> items = client.fetchArticlesWithContent(5);
        return parser.parse(items);
    }

    @Override
    public String getProviderName() {
        return "DevTo";
    }
}

