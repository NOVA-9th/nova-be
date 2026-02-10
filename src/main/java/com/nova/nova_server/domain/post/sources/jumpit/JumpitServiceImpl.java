package com.nova.nova_server.domain.post.sources.jumpit;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JumpitServiceImpl implements ArticleApiService {

    private final JumpitClient client;
    private final JumpitParser parser;

    @Override
    public List<ArticleSource> fetchArticles() {
        // 백엔드(2)와 프론트엔드(1) 데이터 수집
        List<JsonNode> backendItems = client.fetchBackendPositions(1);
        List<JsonNode> frontendItems = client.fetchFrontendPositions(1);

        List<ArticleSource> articles = new ArrayList<>();
        articles.addAll(parser.parse(backendItems));
        articles.addAll(parser.parse(frontendItems));

        return articles;
    }

    @Override
    public String getProviderName() {
        return "Jumpit";
    }
}
