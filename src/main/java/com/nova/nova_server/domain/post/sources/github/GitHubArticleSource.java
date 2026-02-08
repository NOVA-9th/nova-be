package com.nova.nova_server.domain.post.sources.github;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.sources.github.dto.GitHubArticle;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GitHubArticleSource implements ArticleSource {
    private final GitHubArticle article;
    private final GitHubClient client;

    @Override
    public String getUrl() {
        return article.htmlUrl();
    }

    @Override
    public Article fetchArticle() {
        String readme = client.fetchReadme(article.fullName());
        return GitHubParser.toArticle(article, readme);
    }
}
