package com.nova.nova_server.domain.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkSourceTypeCountResponse {
    private Long cardTypeId;
    private String cardTypeName;
    private Long count;
}