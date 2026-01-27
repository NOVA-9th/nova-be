package com.nova.nova_server.domain.post.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.client.GitHubClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.GitHubParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubServiceImpl implements ArticleApiService {

    private final GitHubClient client;
    private final GitHubParser parser;

    @Override
    public List<Article> fetchArticles() {
        List<JsonNode> allItems = new ArrayList<>();

        // 전체, 모바일, 웹, 백엔드에서 각각 3개씩 가져옴(6개월 단위)
        allItems.addAll(client.fetchGlobalTrending(3));

        allItems.addAll(client.fetchMobileTrending(3));

        allItems.addAll(client.fetchWebTrending(3));

        allItems.addAll(client.fetchBackendTrending(3));

        // README
        for (JsonNode item : allItems) {
            if (item.has("full_name") && item.get("full_name").asText().contains("/")) {
                String fullName = item.get("full_name").asText();
                String[] parts = fullName.split("/");
                String owner = parts[0];
                String repo = parts[1];

                String readme = client.fetchReadme(owner, repo);
                if (item instanceof com.fasterxml.jackson.databind.node.ObjectNode objectNode) {
                    objectNode.put("readme_content", readme);
                }
            }
        }

        return parser.parse(allItems);
    }

    @Override
    public String getProviderName() {
        return "GitHub";
    }
}
