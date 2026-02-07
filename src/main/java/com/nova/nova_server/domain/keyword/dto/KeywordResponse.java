package com.nova.nova_server.domain.keyword.dto;

import com.nova.nova_server.domain.keyword.entity.Keyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordResponse {

    private Long id;
    private Long interestId;
    private String name;

    public static KeywordResponse from(Keyword keyword) {
        return KeywordResponse.builder()
                .id(keyword.getId())
                .interestId(keyword.getInterest().getId())
                .name(keyword.getName())
                .build();
    }
}
