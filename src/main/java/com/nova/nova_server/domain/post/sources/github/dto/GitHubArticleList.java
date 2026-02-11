package com.nova.nova_server.domain.post.sources.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubArticleList(@JsonProperty("items") List<GitHubArticle> items) {

    public GitHubArticleList {
        items = items != null ? items : List.of();
    }
}
