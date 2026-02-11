package com.nova.nova_server.domain.post.sources.devto;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.sources.devto.dto.DevToArticle;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DevToArticleSource implements ArticleSource {
    private final DevToClient client;
    private final DevToArticle article;

    @Override
    public String getUrl() {
        return this.article.url();
    }

    @Override
    public Article fetchArticle() {
        String body = client.fetchArticleDetail(this.getUrl());
        return DevToParser.toArticle(article, body);
    }
}
