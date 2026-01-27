package com.nova.nova_server.domain.post.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.client.TechBlogClient;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.parser.TechBlogParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TechBlogServiceImpl implements ArticleApiService {

    private final TechBlogClient client;
    private final TechBlogParser parser;

    // 수집할 기술 블로그 RSS URL 목록(추후 더 추가하면 좋을듯..?)
    private static final Map<String, String> BLOG_RSS_URLS = Map.of(
            "Woowa Bros", "https://techblog.woowahan.com/feed/",
            "Kakao", "https://tech.kakao.com/feed/",
            "Naver D2", "https://d2.naver.com/d2.atom",
            "Daangn", "https://medium.com/feed/daangn",
            "NHN Cloud", "https://meetup.nhncloud.com/rss",
            "Toss", "https://toss.tech/rss.xml"
    );

    @Override
    public List<Article> fetchArticles() {
        List<Article> allArticles = new ArrayList<>();

        // 각 블로그 URL에 대해 순차적으로 요청
        for (Map.Entry<String, String> entry : BLOG_RSS_URLS.entrySet()) {
            String blogName = entry.getKey();
            String rssUrl = entry.getValue();

            try {
                JsonNode jsonNode = client.fetchRssAsJson(rssUrl);
                List<Article> parsedArticles = parser.parse(jsonNode);
                allArticles.addAll(parsedArticles);
            } catch (Exception e) {
                log.error("Failed to fetch articles from {}: {}", blogName, e.getMessage());
            }
        }

        // 최신순 정렬
        allArticles.sort((a1, a2) -> {
            if (a1.publishedAt() == null || a2.publishedAt() == null) return 0;
            return a2.publishedAt().compareTo(a1.publishedAt());
        });

        return allArticles;
    }

    @Override
    public String getProviderName() {
        return "TechBlog";
    }
}