package com.nova.nova_server.domain.post.service;

import com.nova.nova_server.domain.post.client.DevToClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.DevToParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DevToIngestionService {

    private final DevToClient client;
    private final DevToParser parser;

    public List<Article> fetchArticles() {
        try {
            // 원본 JSON 겟
            String json = client.fetchRawJson();
            // Article 객체로 변환
            return parser.parse(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch articles from Dev.to", e);
        }
    }
}