package com.nova.nova_server.domain.batch.service;

import com.nova.nova_server.domain.batch.entity.BatchRunMetadata;
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
 * 뉴스 및 커뮤니티 API들로부터 최신 기사를 수집하는 서비스입니다.
 * - 소스당 최대 수집 개수 제한 (현재 10개)
 * - 마지막으로 배치가 성공했던 시점 이후의 글만 골라서 가져옵니다.
 * - 이미 DB에 있는 글은 URL 중복 체크를 통해 제외합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleFetchService {

    private static final String CARD_NEWS_BATCH_JOB_NAME = "card-news-batch";
    private static final int MAX_ARTICLES_PER_PROVIDER = 10;

    private final List<ArticleApiService> articleApiServices;
    private final BatchRunMetadataRepository batchRunMetadataRepository;
    private final CardNewsRepository cardNewsRepository;

    /**
     * 등록된 모든 API 소스에서 기사를 수집합니다.
     */
    public List<Article> fetchAllArticles() {
        // 테스트 편의성을 위해 마지막 성공 시간 대신 최근 5일 이내의 글을 모두 후보로 둡니다. (중복 URL은 자동으로 걸러짐)
        LocalDateTime lastRunAt = LocalDateTime.now().minusDays(5);

        List<Article> allArticles = new ArrayList<>();
        log.info("Starting article fetch. lastRunAt (fixed to 5 days ago): {}", lastRunAt);

        for (ArticleApiService service : articleApiServices) {
            try {
                List<Article> articles = service.fetchArticles();
                log.info("Fetched {} raw articles from {}", articles.size(), service.getProviderName());
                List<Article> limited = articles.stream()
                        .filter(a -> {
                            boolean isAfter = isAfterLastRun(a, lastRunAt);
                            if (!isAfter) {
                                log.debug("Skipping article (too old): {} (publishedAt: {})", a.title(),
                                        a.publishedAt());
                            }
                            return isAfter;
                        })
                        .filter(a -> {
                            boolean isNotDup = isNotDuplicateByUrl(a);
                            if (!isNotDup) {
                                log.debug("Skipping article (duplicate URL): {}", a.title());
                            }
                            return isNotDup;
                        })
                        .limit(MAX_ARTICLES_PER_PROVIDER)
                        .toList();
                allArticles.addAll(limited);
                log.info("Finished processing {}: {} selected / {} raw fetched", service.getProviderName(),
                        limited.size(), articles.size());
            } catch (Exception e) {
                log.warn("Error in provider {}: {}", service.getProviderName(), e.getMessage());
            }
        }

        log.info("Total articles collected: {} from {} providers", allArticles.size(), articleApiServices.size());
        return allArticles;
    }

    private boolean isAfterLastRun(Article article, LocalDateTime lastRunAt) {
        if (article.publishedAt() == null) {
            return true;
        }
        return article.publishedAt().isAfter(lastRunAt);
    }

    private boolean isNotDuplicateByUrl(Article article) {
        if (article.url() == null || article.url().isBlank()) {
            return false;
        }
        return !cardNewsRepository.existsByOriginalUrl(article.url());
    }
}
