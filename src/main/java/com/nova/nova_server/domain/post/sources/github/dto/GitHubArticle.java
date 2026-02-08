package com.nova.nova_server.domain.post.sources.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubArticle(
        @JsonProperty("full_name") String fullName,
        @JsonProperty("name") String name,
        @JsonProperty("owner") GitHubOwner owner,
        @JsonProperty("description") String description,
        @JsonProperty("language") String language,
        @JsonProperty("stargazers_count") int stargazersCount,
        @JsonProperty("topics") List<String> topics,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
    public String getAuthor() {
        if (owner != null && owner.login() != null) {
            return owner.login();
        }
        else {
            return null;
        }
    }
}