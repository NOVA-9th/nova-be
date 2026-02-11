package com.nova.nova_server.domain.post.sources.github;

import com.nova.nova_server.domain.post.sources.github.dto.GitHubArticleList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubClient {

    private final WebClient webClient;

    @Value("${external.github.base-url}")
    private String baseUrl;

    @Value("${external.github.user-agent}")
    private String userAgent;

    @Value("${external.github.token:}") // 토큰이 없으면 빈 문자열
    private String token;

    // github 공통 검색 메소드
    public GitHubArticleList fetchRepositories(String query, int limit) {
        String uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/search/repositories")
                .queryParam("q", query)
                .queryParam("sort", "stars")
                .queryParam("order", "desc")
                .queryParam("per_page", limit)
                .build()
                .toUriString();

        var request = webClient.get()
                .uri(uri)
                .header("User-Agent", userAgent);

        if (token != null && !token.isBlank()) {
            request.header("Authorization", "Bearer " + token);
        }

        return request
                .retrieve()
                .bodyToMono(GitHubArticleList.class)
                .block();
    }

    // 최근 1개월 전체 인기 프로젝트
    public GitHubArticleList fetchGlobalTrending(int limit) {
        String date = LocalDate.now().minusMonths(1).toString();
        String query = "created:>" + date;
        return fetchRepositories(query, limit);
    }

    // 최근 6개월 모바일 인기 프로젝트
    public GitHubArticleList fetchMobileTrending(int limit) {
        String date = LocalDate.now().minusMonths(6).toString();
        // 해당 토픽이 제목/설명/리드미에 있으면 검색됨 이하동일
        String query = "topic:mobile created:>" + date;
        return fetchRepositories(query, limit);
    }

    // 최근 6개월 웹 인기 프로젝트
    public GitHubArticleList fetchWebTrending(int limit) {
        String date = LocalDate.now().minusMonths(6).toString();
        String query = "topic:frontend created:>" + date;
        return fetchRepositories(query, limit);
    }

    // 최근 6개월 백엔드 인기 프로젝트
    public GitHubArticleList fetchBackendTrending(int limit) {
        String date = LocalDate.now().minusMonths(6).toString();
        String query = "topic:backend created:>" + date;
        return fetchRepositories(query, limit);
    }

    // 레포지토리 README 가져오기
    public String fetchReadme(String fullName) {
        String[] parts = fullName.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("잘못된 이름");
        }

        String owner = parts[0];
        String repo = parts[1];
        return fetchReadme(owner, repo);
    }

    public String fetchReadme(String owner, String repo) {
        String uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/readme")
                .buildAndExpand(owner, repo)
                .toUriString();

        var request = webClient.get()
                .uri(uri)
                .header("User-Agent", userAgent)
                .header("Accept", "application/vnd.github.raw"); // 마크다운으로 가져옴

        if (token != null && !token.isBlank()) {
            request.header("Authorization", "Bearer " + token);
        }

        return request.retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
