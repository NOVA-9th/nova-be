package com.nova.nova_server.domain.post.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.client.NaverNewsApiClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.NaverNewsApiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverNewsApiServiceImpl implements ArticleApiService {

    private final NaverNewsApiClient client;
    private final NaverNewsApiParser parser;

    @Override
    public List<Article> fetchArticles() {
        List<Article> allArticles = new ArrayList<>();

        // Client에서 모든 키워드의 결과를 가져옴
        List<JsonNode> results = client.fetchAll();

        // 각 결과를 파싱해서 합침
        for (JsonNode result : results) {
            List<Article> articles = parser.parse(result);
            allArticles.addAll(articles);
        }

        return allArticles;
    }

    @Override
    public String getProviderName() {
        return "NaverNewsAPI";
    }
}