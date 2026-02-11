package com.nova.nova_server.domain.post.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SelfContainedArticleSource implements ArticleSource {
    private final String url;
    private final Article article;

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Article fetchArticle() {
        return article;
    }
}
