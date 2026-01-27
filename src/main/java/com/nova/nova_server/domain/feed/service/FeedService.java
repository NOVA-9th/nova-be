package com.nova.nova_server.domain.feed.service;

import com.nova.nova_server.domain.feed.dto.FeedListResponse;
import com.nova.nova_server.domain.feed.dto.FeedRequest;
import com.nova.nova_server.domain.feed.dto.FeedResponse;
import com.nova.nova_server.domain.post.model.CardType;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class FeedService {

    public FeedListResponse getCardNews(
            long memberId,
            FeedRequest request
    ) {
        // TODO: 동적쿼리 구현 후 실제 응답 반환
        return FeedListResponse.builder()
                .totalCount(1L)
                .cardnews(
                        List.of(FeedResponse.builder()
                                .id(1L)
                                .title("Nova, 개발자를 위한 AI 트렌드 인사이트 플랫폼")
                                .cardType(CardType.COMMUNITY)
                                .author("nova team")
                                .publishedAt(OffsetDateTime.of(2026, 2, 20, 0, 0, 0, 0, ZoneOffset.UTC))
                                .summary("Nova는 개발자를 위한 AI 트렌드 인사이트 플랫폼으로, 최신 AI 기술과 트렌드를 한눈에 파악할 수 있는 서비스를 제공합니다.")
                                .evidence("")  // TODO: evidence 형식 추가 논의 필요
                                .originalUrl("https://tested-lens-9f2.notion.site/NOVA-2aab07ea6fc680699cafc133762a0632")
                                .siteName("Nova 블로그")
                                .keywords(List.of("Spring Boot", "React", "Next.js", "Java", "TypeScript", "AI"))
                                .saved(true)
                                .build())
                ).build();
    }

}
