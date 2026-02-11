package com.nova.nova_server.domain.post.sources.hackernews;

import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HackerNewsServiceImpl implements ArticleApiService {

    private final HackerNewsClient client;

    @Override
    public List<ArticleSource> fetchArticles() {
        return client.fetchMixedStories();
    }

    @Override
    public String getProviderName() {
        return "HackerNews";
    }
}