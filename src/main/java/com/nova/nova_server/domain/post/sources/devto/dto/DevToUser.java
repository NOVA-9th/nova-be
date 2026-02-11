package com.nova.nova_server.domain.post.sources.devto.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DevToUser(
        String name
) {}
