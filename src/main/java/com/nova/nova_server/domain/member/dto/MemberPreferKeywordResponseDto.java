package com.nova.nova_server.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "사용자 관심 키워드 응답")
public record MemberPreferKeywordResponseDto(
        @Schema(description = "관심 키워드 총 개수", example = "5")
        long totalCount,

        @Schema(description = "관심 키워드 목록", example = "[\"Spring Boot\", \"React\", \"AI\"]")
        List<String> keywords
) {
}
