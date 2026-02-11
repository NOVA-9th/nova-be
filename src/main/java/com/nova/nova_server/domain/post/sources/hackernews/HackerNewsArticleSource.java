package com.nova.nova_server.domain.post.sources.hackernews;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HackerNewsArticleSource implements ArticleSource {
    private final int id;
    private final HackerNewsClient client;

    @Override
    public String getUrl() {
        return client.getArticleUrl(this.id);
    }

    @Override
    public Article fetchArticle() {
        HackerNewsItem item = client.fetchItem(this.id);
        String content = client.fetchContent(item);
        return HackerNewsParser.toArticle(item, content);
    }
}
