package com.nova.nova_server.domain.batch.service;

import com.nova.nova_server.domain.batch.converter.ArticleToPromptConverter;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 카드뉴스 배치 작업 오케스트레이션
 * Article → 프롬프트 변환 → OpenAI Batch 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardNewsBatchService {

    private final ArticleToPromptConverter articleToPromptConverter;
    private final AiBatchService aiBatchService;

    /**
     * Article 목록을 LLM 프롬프트로 변환 후 배치 생성
     *
     * @param articles 아티클 목록
     * @return OpenAI Batch ID (custom_id: article-0, article-1, ... 순서)
     */
    public String createBatchFromArticles(List<Article> articles) {
        if (articles == null || articles.isEmpty()) {
            throw new IllegalArgumentException("articles cannot be empty");
        }

        List<String> promptStrings = articleToPromptConverter.toPromptStrings(articles);
        String batchId = aiBatchService.createBatch(promptStrings);
        log.info("Batch created from {} articles: batchId={}", articles.size(), batchId);
        return batchId;
    }
}
