package com.nova.nova_server.domain.post.controller;

import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.service.ArticleApiServiceFactory;
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

    private final ArticleApiServiceFactory articleApiServiceFactory;
    private final CardNewsRepository cardNewsRepository;

    @Operation(summary = "DB 카드뉴스 총 개수", description = "현재 DB에 저장된 전체 카드뉴스 레코드 수를 반환합니다.")
    @GetMapping("/db-count")
    public long getDbCount() {
        return cardNewsRepository.count();
    }

    @Operation(summary = "제공자별 기사 통합 조회", description = "모든 뉴스 및 커뮤니티 소스로부터 기사 데이터를 한꺼번에 긁어옵니다.")
    @GetMapping("/all-articles")
    public Map<String, List<Article>> fetchAllArticlesFromProviders() {
        return articleApiServiceFactory.createAllAvailableServices().stream()
                .collect(Collectors.toMap(
                        ArticleApiService::getProviderName,
                        service -> {
                            try {
                                return service.fetchArticles()
                                        .stream()
                                        .map(source -> {
                                            try {
                                                return source.fetchArticle();
                                            } catch (Exception e) {
                                                log.warn("Failed to fetch article from {}", service.getProviderName(), e);
                                                return null;
                                            }
                                        })
                                        .toList();
                            }
                            catch (Exception e) {
                                log.warn("Failed to fetch articles from {}", service.getProviderName(), e);
                                return List.of();
                            }
                        }
                ));
    }

    @Operation(summary = "제공자별 기사 통합 조회", description = "모든 뉴스 및 커뮤니티 소스로부터 기사 데이터를 한꺼번에 긁어옵니다.")
    @GetMapping("/all-article-sources")
    public Map<String, List<ArticleSource>> fetchAllArticlesSourceFromProviders() {
        return articleApiServiceFactory.createAllAvailableServices().stream()
                .collect(Collectors.toMap(
                        ArticleApiService::getProviderName,
                        service -> {
                            try {
                                return service.fetchArticles();
                            } catch (Exception e) {
                                log.warn("Failed to fetch articles from {}", service.getProviderName(), e);
                                return List.of();
                            }
                        }
                ));
    }

    @Operation(summary = "사용 가능 제공자 목록", description = "현재 시스템에 등록된 기사 수집 소스(Provider) 목록을 반환합니다.")
    @GetMapping("/providers")
    public List<String> getAvailableProviders() {
        return articleApiServiceFactory.createAllAvailableServices().stream()
                .map(ArticleApiService::getProviderName)
                .collect(Collectors.toList());
    }

    // --- 개별 뉴스 API 소스 ---

    @Operation(summary = "[뉴스] NewsAPI 호출", description = "NewsAPI에서 최신 기사를 가져옵니다.")
    @GetMapping("/newsapi")
    public List<Article> fetchFromNewsApi() { return fetchAllArticles("NewsAPI"); }

    @Operation(summary = "[뉴스] Naver News Search 호출", description = "네이버 뉴스 검색 결과를 가져옵니다.")
    @GetMapping("/navernewssearch")
    public List<Article> fetchFromNaverNews() { return fetchAllArticles("NaverNewsSearch"); }

    @Operation(summary = "[뉴스] DeepSearch 호출", description = "DeepSearch API에서 데이터를 가져옵니다.")
    @GetMapping("/deepsearch")
    public List<Article> fetchFromDeepSearch() { return fetchAllArticles("DeepSearch"); }

    @Operation(summary = "[뉴스] NewsData 호출", description = "NewsData.io에서 데이터를 가져옵니다.")
    @GetMapping("/newsdata")
    public List<Article> fetchFromNewsData() { return fetchAllArticles("NewsData"); }

    @Operation(summary = "[뉴스] GNews 호출", description = "GNews API에서 데이터를 가져옵니다.")
    @GetMapping("/gnews")
    public List<Article> fetchFromGNews() { return fetchAllArticles("GNews"); }

    @Operation(summary = "[뉴스] Naver News API 호출", description = "네이버 뉴스 공식 API를 통해 데이터를 가져옵니다.")
    @GetMapping("/navernewsapi")
    public List<Article> fetchFromNaverNewsApi() { return fetchAllArticles("NaverNewsAPI"); }

    @Operation(summary = "[뉴스] Hacker News 호출", description = "Hacker News의 최신 인기 게시글을 가져옵니다.")
    @GetMapping("/hackernews")
    public List<Article> fetchFromHackerNews() { return fetchAllArticles("HackerNews"); }

    // --- 커뮤니티 소스 ---

    @Operation(summary = "[커뮤니티] Dev.to 호출", description = "Dev.to의 최신 개발 포스트를 가져옵니다.")
    @GetMapping("/devto")
    public List<Article> fetchFromDevTo() { return fetchAllArticles("DevTo"); }

    @Operation(summary = "[커뮤니티] GitHub 호출", description = "GitHub Trending 포스트를 가져옵니다.")
    @GetMapping("/github")
    public List<Article> fetchFromGitHub() { return fetchAllArticles("GitHub"); }

    @Operation(summary = "[커뮤니티] StackExchange 호출", description = "StackExchange 질문 목록을 가져옵니다.")
    @GetMapping("/stackexchange")
    public List<Article> fetchFromStackExchange() { return fetchAllArticles("StackExchange"); }

    @Operation(summary = "[커뮤니티] TechBlog 호출", description = "기술 블로그 크롤링 데이터를 가져옵니다.")
    @GetMapping("/techblog")
    public List<Article> fetchFromTechBlog() { return fetchAllArticles("TechBlog"); }

    private List<Article> fetchAllArticles(String providerName) {
        ArticleApiService service = articleApiServiceFactory.getServiceByName(providerName);
        return service.fetchArticles()
                .stream()
                .map(ArticleSource::fetchArticle)
                .toList();
    }
}