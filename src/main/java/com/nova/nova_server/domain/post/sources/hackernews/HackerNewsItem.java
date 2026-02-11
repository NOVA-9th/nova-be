package com.nova.nova_server.domain.post.sources.hackernews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HackerNewsItem(
        Integer id,
        String type,
        String title,
        @JsonProperty("by") String by,
        String text,
        String url,
        Long time
) {}
