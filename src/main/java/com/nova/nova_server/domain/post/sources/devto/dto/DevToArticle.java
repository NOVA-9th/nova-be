package com.nova.nova_server.domain.post.sources.devto.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DevToArticle(
        String title,
        String bodyMarkdown,
        String description,
        DevToUser user,
        String url,
        String publishedAt
) {}
