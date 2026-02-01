package com.nova.nova_server.domain.post.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.github.base-url}")
    private String baseUrl;

    @Value("${external.github.user-agent}")
    private String userAgent;

    // github 공통 검색 메소드
    public List<JsonNode> fetchRepositories(String query, int limit) {
        try {
            WebClient client = webClientBuilder.clone()
                    .baseUrl(baseUrl)
                    .defaultHeader("User-Agent", userAgent)
                    .build();

            JsonNode response = client
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/repositories")
                            .queryParam("q", query)
                            .queryParam("sort", "stars")
                            .queryParam("order", "desc")
                            .queryParam("per_page", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("items")) {
                List<JsonNode> items = new ArrayList<>();
                response.get("items").forEach(items::add);
                return items;
            }
        } catch (Exception e) {
            log.error("Github 레포지토리 가져오기 실패: {}", query, e);
        }
        return new ArrayList<>();
    }

    // 최근 1개월 전체 인기 프로젝트
    public List<JsonNode> fetchGlobalTrending(int limit) {
        String date = LocalDate.now().minusMonths(1).toString();
        String query = "created:>" + date;
        return fetchRepositories(query, limit);
    }

    // 최근 6개월 모바일 인기 프로젝트
    public List<JsonNode> fetchMobileTrending(int limit) {
        String date = LocalDate.now().minusMonths(6).toString();
        // 해당 토픽이 제목/설명/리드미에 있으면 검색됨 이하동일
        String query = "topic:mobile created:>" + date;
        return fetchRepositories(query, limit);
    }

    // 최근 6개월 웹 인기 프로젝트
    public List<JsonNode> fetchWebTrending(int limit) {
        String date = LocalDate.now().minusMonths(6).toString();
        String query = "topic:frontend created:>" + date;
        return fetchRepositories(query, limit);
    }

    // 최근 6개월 백엔드 인기 프로젝트
    public List<JsonNode> fetchBackendTrending(int limit) {
        String date = LocalDate.now().minusMonths(6).toString();
        String query = "topic:backend created:>" + date;
        return fetchRepositories(query, limit);
    }

    // 레포지토리 README 가져오기
    public String fetchReadme(String owner, String repo) {
        try {
            WebClient client = WebClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader("User-Agent", userAgent)
                    .build();

            return client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/repos/{owner}/{repo}/readme")
                            .build(owner, repo))
                    .header("Accept", "application/vnd.github.raw") // 마크다운으로 가져옴
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.warn("README 가져오기 실패: {}/{}", owner, repo, e);
        }
        return "";
    }
}
