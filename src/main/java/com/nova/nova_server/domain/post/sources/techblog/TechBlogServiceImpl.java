package com.nova.nova_server.domain.post.sources.techblog;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TechBlogServiceImpl implements ArticleApiService {
    private final TechBlogClient client;
    private final String url;
    private final String name;

    @Override
    public List<ArticleSource> fetchArticles() {
        JsonNode jsonNode = client.fetchRssAsJson(url);
        return TechBlogParser.parse(jsonNode);
    }

    @Override
    public String getProviderName() {
        return "TechBlog-" + name;
    }
}