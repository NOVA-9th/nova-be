package com.nova.nova_server.domain.post.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.client.NaverNewsClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.NaverNewsParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverNewsServiceImpl implements NaverNewsService {

    private final NaverNewsClient naverClient;
    private final NaverNewsParser naverParser;

    @Override
    public List<Article> fetchArticles() {
        JsonNode raw = naverClient.fetch("AI OR 인공지능 OR 블록체인 OR 개발자");
        return naverParser.parse(raw);
    }
}
