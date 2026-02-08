package com.nova.nova_server.domain.post.sources.navernews;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverNewsApiServiceImpl implements ArticleApiService {

    private final NaverNewsApiClient client;

    @Override
    public List<ArticleSource> fetchArticles() {
        List<ArticleSource> allArticles = new ArrayList<>();

        // Client에서 모든 키워드의 결과를 가져옴
        List<JsonNode> results = client.fetchAll();

        // 각 결과를 파싱해서 합침
        for (JsonNode result : results) {
            List<ArticleSource> articles = NaverNewsApiParser.parse(result);
            allArticles.addAll(articles);
        }

        return allArticles;
    }

    @Override
    public String getProviderName() {
        return "NaverNewsAPI";
    }
}