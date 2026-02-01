package com.nova.nova_server.domain.feed.dto;

import com.nova.nova_server.domain.cardNews.entity.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Schema(description = "피드 단건")
public record FeedResponse(
        @Schema(description = "카드뉴스 ID", example = "1")
        long id,

        @Schema(description = "카드뉴스 제목", example = "Nova, 개발자를 위한 AI 트렌드 인사이트 플랫폼")
        String title,

        @Schema(description = "카드뉴스 유형", example = "COMMUNITY")
        CardType cardType,

        @Schema(description = "작성자", example = "nova team")
        String author,

        @Schema(description = "발행일시 (UTC 기준)", example = "2026-01-01T15:00:00Z")
        OffsetDateTime publishedAt,

        @Schema(description = "요약 내용", example = "Nova는 개발자를 위한 AI 트렌드 인사이트 플랫폼으로, 최신 AI 기술과 트렌드를 한눈에 파악할 수 있는 서비스를 제공합니다.")
        String summary,

        @Schema(description = "근거", example = "주기적으로 최신 글을 확인하고 카드뉴스를 생성합니다.")
        String evidence,

        @Schema(description = "원본 URL", example = "https://github.com/NOVA-9th/nova-be")
        String originalUrl,

        @Schema(description = "사이트 이름", example = "Github")
        String siteName,

        @Schema(description = "키워드 목록", example = "[\"Spring Boot\", \"React\", \"Next.js\", \"Java\", \"TypeScript\", \"AI\"]")
        List<String> keywords,

        @Schema(description = "저장 여부", example = "false")
        boolean saved
) {
}
