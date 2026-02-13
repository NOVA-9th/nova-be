package com.nova.nova_server.domain.batch.cardnews.service;

import com.nova.nova_server.domain.batch.common.converter.ArticleConverter;
import com.nova.nova_server.domain.batch.summary.dto.LlmSummaryResult;
import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.entity.CardNewsKeyword;
import com.nova.nova_server.domain.cardNews.repository.CardNewsKeywordRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.keyword.repository.KeywordRepository;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardNewsSaveService {
    private final CardNewsRepository cardNewsRepository;
    private final KeywordRepository keywordRepository;
    private final CardNewsKeywordRepository cardNewsKeywordRepository;

    public void saveSingleCardNews(ArticleEntity entity, LlmSummaryResult llmSummaryResult) {
        Article article = ArticleConverter.toDomain(entity);

        CardNews cardNews = CardNews.builder()
                .cardType(article.cardType())
                .title(article.title())
                .author(article.author())
                .publishedAt(article.publishedAt())
                .summary(llmSummaryResult.summary())
                .evidence(serializeEvidences(llmSummaryResult.evidence()))
                .originalUrl(article.url())
                .sourceSiteName(article.source())
                .build();

        CardNews savedCardNews = cardNewsRepository.save(cardNews);

        // 검색된 키워드들과 엔티티 연결 및 저장
        if (llmSummaryResult.keywords() != null) {
            Set<String> uniqueKeywords = llmSummaryResult.keywords().stream()
                    .filter(keyword -> keyword != null && !keyword.isBlank())
                    .map(String::trim)
                    .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

            for (String keywordName : uniqueKeywords) {
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
    }

    private String serializeEvidences(List<String> evidences) {
        if (evidences == null || evidences.isEmpty()) {
            return null;
        }
        return evidences.stream()
                .map(e -> e.replace("\n", " ").replace("\r", " ").trim())
                .collect(java.util.stream.Collectors.joining("\n"));
    }
}
