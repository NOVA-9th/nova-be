package com.nova.nova_server.domain.keyword.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordCreateRequest {

    @NotNull(message = "관심분야 ID는 필수입니다.")
    private Long interestId;

    @NotBlank(message = "키워드 이름은 필수입니다.")
    private String name;
}
