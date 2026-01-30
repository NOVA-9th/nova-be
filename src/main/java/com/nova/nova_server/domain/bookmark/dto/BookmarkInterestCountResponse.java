package com.nova.nova_server.domain.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkInterestCountResponse {
    private Long interestId;
    private String interestName;
    private Long count;
}