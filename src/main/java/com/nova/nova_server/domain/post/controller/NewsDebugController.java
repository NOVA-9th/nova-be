package com.nova.nova_server.domain.post.controller;

import com.nova.nova_server.domain.batch.entity.BatchRunMetadata;
import com.nova.nova_server.domain.batch.repository.BatchRunMetadataRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/debug/news")
public class NewsDebugController {

    private final List<ArticleApiService> articleApiServices;
    private final CardNewsRepository cardNewsRepository;
    private final BatchRunMetadataRepository batchRunMetadataRepository;

    @GetMapping("/db-count")
    public long getDbCount() {
        return cardNewsRepository.count();
    }

    @GetMapping("/latest-metadata")
    public Optional<BatchRunMetadata> getLatestMetadata() {
        return batchRunMetadataRepository.findTopByJobNameAndStatusNotOrderByExecutedAtDesc("card-news-batch",
                "RUNNING");
    }

    @GetMapping("/newsapi")
    public List<Article> fetchFromNewsApi() {
        return getServiceByName("NewsAPI").fetchArticles();
    }

    @GetMapping("/navernewssearch")
    public List<Article> fetchFromNaverNews() {
        return getServiceByName("NaverNewsSearch").fetchArticles();
    }

    @GetMapping("/deepsearch")
    public List<Article> fetchFromDeepSearch() {
        return getServiceByName("DeepSearch").fetchArticles();
    }

    @GetMapping("/newsdata")
    public List<Article> fetchFromNewsData() {
        return getServiceByName("NewsData").fetchArticles();
    }

    @GetMapping("/gnews")
    public List<Article> fetchFromGNews() {
        return getServiceByName("GNews").fetchArticles();
    }

    @GetMapping("/navernewsapi")
    public List<Article> fetchFromNaverNewsApi() {
        return getServiceByName("NaverNewsAPI").fetchArticles();
    }

    @GetMapping("/hackernews")
    public List<Article> fetchFromHackerNews() {
        return getServiceByName("HackerNews").fetchArticles();
    }

    // COMMUNITY
    @GetMapping("/devto")
    public List<Article> fetchFromDevTo() {
        return getServiceByName("DevTo").fetchArticles();
    }

    @GetMapping("/github")
    public List<Article> fetchFromGitHub() {
        return getServiceByName("GitHub").fetchArticles();
    }

    @GetMapping("/stackexchange")
    public List<Article> fetchFromStackExchange() {
        return getServiceByName("StackExchange").fetchArticles();
    }

    @GetMapping("/techblog")
    public List<Article> fetchFromTechBlog() {
        return getServiceByName("TechBlog").fetchArticles();
    }

    // 모든 Provider 호출 (통합 조회)
    @GetMapping("/all")
    public Map<String, List<Article>> fetchFromAllSources() {
        return articleApiServices.stream()
                .collect(Collectors.toMap(
                        ArticleApiService::getProviderName,
                        service -> {
                            try {
                                return service.fetchArticles();
                            } catch (Exception e) {
                                // 로그 추가
                                log.warn("Failed to fetch articles from {}", service.getProviderName(), e);
                                return List.of();
                            }
                        }
                ));
    }

    // 사용 가능한 API 목록 조회
    @GetMapping("/providers")
    public List<String> getAvailableProviders() {
        return articleApiServices.stream()
                .map(ArticleApiService::getProviderName)
                .collect(Collectors.toList());
    }

    private ArticleApiService getServiceByName(String providerName) {
        return articleApiServices.stream()
                .filter(service -> service.getProviderName().equals(providerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown provider: " + providerName));
    }
}