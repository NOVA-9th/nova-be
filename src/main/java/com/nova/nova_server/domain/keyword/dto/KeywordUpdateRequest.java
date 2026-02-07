package com.nova.nova_server.domain.keyword.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordUpdateRequest {

    private Long interestId;
    private String name;
}
