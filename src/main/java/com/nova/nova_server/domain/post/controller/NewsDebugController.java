package com.nova.nova_server.domain.post.controller;

import com.nova.nova_server.domain.batch.entity.BatchRunMetadata;
import com.nova.nova_server.domain.batch.repository.BatchRunMetadataRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Debug: News/Batch", description = "뉴스 데이터 수집 및 배치 모니터링 디버그 API")
public class NewsDebugController {

    private final List<ArticleApiService> articleApiServices;
    private final CardNewsRepository cardNewsRepository;
    private final BatchRunMetadataRepository batchRunMetadataRepository;

    @Operation(summary = "DB 카드뉴스 총 개수", description = "현재 DB에 저장된 전체 카드뉴스 레코드 수를 반환합니다.")
    @GetMapping("/db-count")
    public long getDbCount() {
        return cardNewsRepository.count();
    }

    @Operation(summary = "최근 배치 실행 메타데이터", description = "마지막으로 실행된 카드뉴스 배치 작업의 상태와 시간을 조회합니다.")
    @GetMapping("/latest-metadata")
    public Optional<BatchRunMetadata> getLatestMetadata() {
        return batchRunMetadataRepository.findTopByJobNameAndStatusNotOrderByExecutedAtDesc("card-news-batch",
                "RUNNING");
    }

    @Operation(summary = "제공자별 기사 통합 조회", description = "모든 뉴스 및 커뮤니티 소스로부터 기사 데이터를 한꺼번에 긁어옵니다.")
    @GetMapping("/all")
    public Map<String, List<Article>> fetchFromAllSources() {
        return articleApiServices.stream()
                .collect(Collectors.toMap(
                        ArticleApiService::getProviderName,
                        service -> {
                            try {
                                return service.fetchArticles();
                            } catch (Exception e) {
                                log.warn("Failed to fetch articles from {}", service.getProviderName(), e);
                                return List.of();
                            }
                        }));
    }

    @Operation(summary = "사용 가능 제공자 목록", description = "현재 시스템에 등록된 기사 수집 소스(Provider) 목록을 반환합니다.")
    @GetMapping("/providers")
    public List<String> getAvailableProviders() {
        return articleApiServices.stream()
                .map(ArticleApiService::getProviderName)
                .collect(Collectors.toList());
    }

    // --- 개별 뉴스 API 소스 ---

    @Operation(summary = "[뉴스] NewsAPI 호출", description = "NewsAPI에서 최신 기사를 가져옵니다.")
    @GetMapping("/newsapi")
    public List<Article> fetchFromNewsApi() {
        return getServiceByName("NewsAPI").fetchArticles();
    }

    @Operation(summary = "[뉴스] Naver News Search 호출", description = "네이버 뉴스 검색 결과를 가져옵니다.")
    @GetMapping("/navernewssearch")
    public List<Article> fetchFromNaverNews() {
        return getServiceByName("NaverNewsSearch").fetchArticles();
    }

    @Operation(summary = "[뉴스] DeepSearch 호출", description = "DeepSearch API에서 데이터를 가져옵니다.")
    @GetMapping("/deepsearch")
    public List<Article> fetchFromDeepSearch() {
        return getServiceByName("DeepSearch").fetchArticles();
    }

    @Operation(summary = "[뉴스] NewsData 호출", description = "NewsData.io에서 데이터를 가져옵니다.")
    @GetMapping("/newsdata")
    public List<Article> fetchFromNewsData() {
        return getServiceByName("NewsData").fetchArticles();
    }

    @Operation(summary = "[뉴스] GNews 호출", description = "GNews API에서 데이터를 가져옵니다.")
    @GetMapping("/gnews")
    public List<Article> fetchFromGNews() {
        return getServiceByName("GNews").fetchArticles();
    }

    @Operation(summary = "[뉴스] Naver News API 호출", description = "네이버 뉴스 공식 API를 통해 데이터를 가져옵니다.")
    @GetMapping("/navernewsapi")
    public List<Article> fetchFromNaverNewsApi() {
        return getServiceByName("NaverNewsAPI").fetchArticles();
    }

    @Operation(summary = "[뉴스] Hacker News 호출", description = "Hacker News의 최신 인기 게시글을 가져옵니다.")
    @GetMapping("/hackernews")
    public List<Article> fetchFromHackerNews() {
        return getServiceByName("HackerNews").fetchArticles();
    }

    // --- 커뮤니티 소스 ---

    @Operation(summary = "[커뮤니티] Dev.to 호출", description = "Dev.to의 최신 개발 포스트를 가져옵니다.")
    @GetMapping("/devto")
    public List<Article> fetchFromDevTo() {
        return getServiceByName("DevTo").fetchArticles();
    }

    @Operation(summary = "[커뮤니티] GitHub 호출", description = "GitHub Trending 포스트를 가져옵니다.")
    @GetMapping("/github")
    public List<Article> fetchFromGitHub() {
        return getServiceByName("GitHub").fetchArticles();
    }

    @Operation(summary = "[커뮤니티] StackExchange 호출", description = "StackExchange 질문 목록을 가져옵니다.")
    @GetMapping("/stackexchange")
    public List<Article> fetchFromStackExchange() {
        return getServiceByName("StackExchange").fetchArticles();
    }

    @Operation(summary = "[커뮤니티] TechBlog 호출", description = "기술 블로그 크롤링 데이터를 가져옵니다.")
    @GetMapping("/techblog")
    public List<Article> fetchFromTechBlog() {
        return getServiceByName("TechBlog").fetchArticles();
    }

    @Operation(summary = "[취업] Jumpit 호출", description = "점핏에서 최신 백엔드 채용 공고를 가져옵니다.")
    @GetMapping("/jumpit")
    public List<Article> fetchFromJumpit() {
        return getServiceByName("Jumpit").fetchArticles();
    }

    private ArticleApiService getServiceByName(String providerName) {
        return articleApiServices.stream()
                .filter(service -> service.getProviderName().equals(providerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown provider: " + providerName));
    }
}