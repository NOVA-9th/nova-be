package com.nova.nova_server.domain.post.sources.devto;

import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DevToServiceImpl implements ArticleApiService {
    private final DevToClient client;

    @Override
    public List<ArticleSource> fetchArticles() {
        return client.fetchArticles().stream()
                .map(article -> (ArticleSource)new DevToArticleSource(client, article))
                .toList();
    }

    @Override
    public String getProviderName() {
        return "DevTo";
    }
}

