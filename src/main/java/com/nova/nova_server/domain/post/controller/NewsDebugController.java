package com.nova.nova_server.domain.post.controller;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/debug/news")
public class NewsDebugController {

    private final List<ArticleApiService> articleApiServices;

    @GetMapping("/newsapi")
    public List<Article> fetchFromNewsApi() {
        return getServiceByName("NewsAPI").fetchArticles();
    }

    @GetMapping("/naversearch")
    public List<Article> fetchFromNaverNews() {
        return getServiceByName("NaverNews").fetchArticles();
    }

    @GetMapping("/deepsearch")
    public List<Article> fetchFromDeepSearch() {
        return getServiceByName("DeepSearch").fetchArticles();
    }

    @GetMapping("/newsdata")
    public List<Article> fetchFromNewsData() {
        return getServiceByName("NewsData").fetchArticles();
    }

    //모든 API에서 기사 조회 (통합)
    @GetMapping("/all")
    public Map<String, List<Article>> fetchFromAllSources() {
        return articleApiServices.stream()
                .collect(Collectors.toMap(
                        ArticleApiService::getProviderName,
                        service -> {
                            try {
                                return service.fetchArticles();
                            } catch (Exception e) {
                                return List.of(); // 실패 시 빈 리스트 반환
                            }
                        }
                ));
    }

    //사용 가능한 API 목록 조회
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