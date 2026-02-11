package com.nova.nova_server.domain.post.sources.newsapi;

import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsApiServiceImpl implements ArticleApiService {

    private final NewsApiClient client;

    @Override
    public List<ArticleSource> fetchArticles() {
        String json = client.fetchRawJson();
        return NewsApiParser.parse(json);
    }

    @Override
    public String getProviderName() {
        return "NewsAPI";
    }
}