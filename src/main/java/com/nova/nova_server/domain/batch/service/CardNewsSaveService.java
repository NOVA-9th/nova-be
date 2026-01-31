package com.nova.nova_server.domain.batch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.batch.dto.LlmSummaryResult;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.cardType.entity.CardType;
import com.nova.nova_server.domain.cardType.repository.CardTypeRepository;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * LLM 결과 + Article → CardNews 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardNewsSaveService {

    private final CardNewsRepository cardNewsRepository;
    private final CardTypeRepository cardTypeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * LLM 결과와 Article 목록을 매핑하여 CardNews 저장
     *
     * @param articles        원본 아티클 목록 (custom_id 순서: article-0, article-1, ...)
     * @param llmResults      custom_id → LLM 응답 JSON 매핑 (실패 시 null)
     * @return 저장된 CardNews 개수
     */
    @Transactional
    public int saveCardNews(List<Article> articles, Map<String, String> llmResults) {
        int savedCount = 0;

        for (int i = 0; i < articles.size(); i++) {
            String customId = "article-" + i;
            Article article = articles.get(i);
            String llmJson = llmResults.get(customId);

            if (llmJson == null) {
                log.warn("LLM result is null for customId={}, skipping", customId);
                continue;
            }

            try {
                LlmSummaryResult result = parseLlmResult(llmJson);
                CardType cardType = resolveCardType(article.cardType());

                CardNews cardNews = CardNews.builder()
                        .cardType(cardType)
                        .title(article.title())
                        .author(article.author())
                        .publishedAt(article.publishedAt())
                        .summary(result.getSummary())
                        .evidence(result.getEvidence())
                        .originalUrl(article.url())
                        .sourceSiteName(article.source())
                        .build();

                cardNewsRepository.save(cardNews);
                savedCount++;
                log.debug("CardNews saved: title={}", article.title());
            } catch (Exception e) {
                log.warn("Failed to save CardNews for customId={}: {}", customId, e.getMessage());
            }
        }

        log.info("CardNews saved: {}/{}", savedCount, articles.size());
        return savedCount;
    }

    private LlmSummaryResult parseLlmResult(String llmJson) {
        try {
            // LLM 응답에서 JSON 부분만 추출 (마크다운 코드 블럭 등 제거)
            String json = extractJson(llmJson);
            return objectMapper.readValue(json, LlmSummaryResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LLM result: " + llmJson, e);
        }
    }

    private String extractJson(String content) {
        // ```json ... ``` 형태로 감싸져 있을 수 있음
        if (content.contains("```")) {
            int start = content.indexOf("{");
            int end = content.lastIndexOf("}");
            if (start >= 0 && end > start) {
                return content.substring(start, end + 1);
            }
        }
        return content.trim();
    }

    private CardType resolveCardType(com.nova.nova_server.domain.post.model.CardType articleCardType) {
        String typeName = switch (articleCardType) {
            case NEWS -> "NEWS";
            case JOB -> "JOB";
            case COMMUNITY -> "COMMUNITY";
        };

        return cardTypeRepository.findByName(typeName)
                .orElseThrow(() -> new IllegalStateException("CardType not found: " + typeName));
    }
}
