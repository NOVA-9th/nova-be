package com.nova.nova_server.domain.post.sources.gnews;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GNewsServiceImpl implements ArticleApiService {

    private final GNewsClient client;

    @Override
    public List<ArticleSource> fetchArticles() {
        String json = client.fetchRawJson();
        return GNewsParser.parse(json);
    }

    @Override
    public String getProviderName() {
        return "GNews";
    }
}