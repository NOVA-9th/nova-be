package com.nova.nova_server.domain.post.sources.github;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.model.SelfContainedArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.sources.github.dto.GitHubArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubServiceImpl implements ArticleApiService {

    private final GitHubClient client;

    @Override
    public List<ArticleSource> fetchArticles() {
        List<GitHubArticle> allItems = new ArrayList<>();
        allItems.addAll(client.fetchGlobalTrending(100).items());
        allItems.addAll(client.fetchMobileTrending(5).items());
        allItems.addAll(client.fetchWebTrending(5).items());
        allItems.addAll(client.fetchBackendTrending(5).items());

        return allItems.stream()
                .map(item -> (ArticleSource)new GitHubArticleSource(item, client))
                .toList();
    }

    @Override
    public String getProviderName() {
        return "GitHub";
    }
}
