package com.nova.nova_server.domain.post.service;

import com.nova.nova_server.domain.post.model.Article;

import java.util.List;

public interface NaverNewsSearchService {
    List<Article> fetchArticles();
}
