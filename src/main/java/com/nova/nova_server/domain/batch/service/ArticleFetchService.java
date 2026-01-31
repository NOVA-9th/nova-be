package com.nova.nova_server.domain.batch.service;

import com.nova.nova_server.domain.batch.repository.BatchRunMetadataRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 뉴스/커뮤니티 API에서 아티클 수집
 * - 각 클라이언트당 최대 3개
 * - 증분 처리: 마지막 배치 실행 시점 이후 발행된 글만 수집
 * - URL 중복 방지: 이미 DB에 존재하는 URL 제외
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleFetchService {

    private static final String CARD_NEWS_BATCH_JOB_NAME = "card-news-batch";
    private static final int MAX_ARTICLES_PER_PROVIDER = 3;

    private final List<ArticleApiService> articleApiServices;
    private final BatchRunMetadataRepository batchRunMetadataRepository;
    private final CardNewsRepository cardNewsRepository;

    /**
     * 모든 Provider에서 아티클 수집 (클라이언트당 최대 3개, 증분 필터 적용)
     */
    public List<Article> fetchAllArticles() {
        LocalDateTime lastRunAt = batchRunMetadataRepository
                .findTopByJobNameOrderByExecutedAtDesc(CARD_NEWS_BATCH_JOB_NAME)
                .map(meta -> meta.getExecutedAt())
                .orElse(LocalDateTime.MIN);

        List<Article> allArticles = new ArrayList<>();

        for (ArticleApiService service : articleApiServices) {
            try {
                List<Article> articles = service.fetchArticles();
                List<Article> limited = articles.stream()
                        .limit(MAX_ARTICLES_PER_PROVIDER)
                        .filter(a -> isAfterLastRun(a, lastRunAt))
                        .filter(this::isNotDuplicateByUrl)
                        .toList();
                allArticles.addAll(limited);
                log.debug("Fetched {} articles from {} (after filter)", limited.size(), service.getProviderName());
            } catch (Exception e) {
                log.warn("Failed to fetch articles from {}: {}", service.getProviderName(), e.getMessage(), e);
            }
        }

        log.info("Total articles collected: {} from {} providers", allArticles.size(), articleApiServices.size());
        return allArticles;
    }

    private boolean isAfterLastRun(Article article, LocalDateTime lastRunAt) {
        if (article.publishedAt() == null) {
            return true;
        }
        // 마지막 배치 실행 시점 이후 발행된 글만 수집
        return article.publishedAt().isAfter(lastRunAt);
    }

    private boolean isNotDuplicateByUrl(Article article) {
        if (article.url() == null || article.url().isBlank()) {
            return false;
        }
        return !cardNewsRepository.existsByOriginalUrl(article.url());
    }
}
