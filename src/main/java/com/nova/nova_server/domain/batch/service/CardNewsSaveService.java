package com.nova.nova_server.domain.batch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.batch.dto.LlmSummaryResult;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.entity.CardNewsKeyword;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.cardNews.repository.CardNewsKeywordRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.keyword.repository.KeywordRepository;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * AI가 요약한 결과와 원본 기사 데이터를 조합해서 최종적으로 카드 뉴스로 저장하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardNewsSaveService {

    private final CardNewsRepository cardNewsRepository;
    private final KeywordRepository keywordRepository;
    private final CardNewsKeywordRepository cardNewsKeywordRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * AI 응답 결과를 파싱해서 각각의 기사와 매칭해 DB에 저장
     *
     * @param articles   수집된 원본 기사 리스트
     * @param llmResults OpenAI로부터 받은 custom_id별 요약 결과
     * @return 성공적으로 저장된 카드 뉴스 개수
     */
    // @Transactional을 제거하여 루프 내의 개별 저장이 별도 트랜잭션으로 동작하게 함 (또는 개별 메소드 호출)
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
                saveSingleCardNews(article, llmJson, customId);
                savedCount++;
            } catch (Exception e) {
                log.warn("Failed to save CardNews for customId={}: {}", customId, e.getMessage());
            }
        }

        log.info("CardNews saved: {}/{}", savedCount, articles.size());
        return savedCount;
    }

    @Transactional
    public void saveSingleCardNews(Article article, String llmJson, String customId) {
        LlmSummaryResult result = parseLlmResult(llmJson);

        CardNews cardNews = CardNews.builder()
                .cardType(article.cardType())
                .title(article.title())
                .author(article.author())
                .publishedAt(article.publishedAt())
                .summary(result.summary())
                .evidence(result.evidence() != null ? result.evidence().stream()
                        .map(e -> e.replace("\n", " ").replace("\r", " ").trim())
                        .collect(java.util.stream.Collectors.joining("\n")) : null)
                .originalUrl(article.url())
                .sourceSiteName(article.source())
                .build();

        CardNews savedCardNews = cardNewsRepository.save(cardNews);

        // 검색된 키워드들과 엔티티 연결 및 저장
        if (result.keywords() != null) {
            for (String keywordName : result.keywords()) {
                keywordRepository.findByName(keywordName.trim()).ifPresent(keyword -> {
                    CardNewsKeyword cardNewsKeyword = CardNewsKeyword.builder()
                            .cardNewsId(savedCardNews.getId())
                            .keywordId(keyword.getId())
                            .cardNews(savedCardNews)
                            .keyword(keyword)
                            .build();
                    cardNewsKeywordRepository.save(cardNewsKeyword);
                });
            }
        }
        log.debug("CardNews saved with keywords: title={}", article.title());
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
}
