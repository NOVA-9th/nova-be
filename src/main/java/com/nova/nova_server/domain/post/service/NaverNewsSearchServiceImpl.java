package com.nova.nova_server.domain.post.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.client.NaverNewsSearchClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.NaverNewsSearchParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverNewsSearchServiceImpl implements ArticleApiService {

    private final NaverNewsSearchClient naverClient;
    private final NaverNewsSearchParser naverParser;

    @Override
    public List<Article> fetchArticles() {
        JsonNode raw = naverClient.fetch("AI OR 인공지능 OR 블록체인 OR 개발자");
        return naverParser.parse(raw);
    }

    @Override
    public String getProviderName() {
        return "NaverNews";
    }
}