package com.nova.nova_server.domain.post.sources.techblog;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.model.Article;
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
    public List<ArticleSource> fetchArticles() {
        List<ArticleSource> allArticles = new ArrayList<>();

        // 각 블로그 URL에 대해 순차적으로 요청
        for (Map.Entry<String, String> entry : BLOG_RSS_URLS.entrySet()) {
            String rssUrl = entry.getValue();

            JsonNode jsonNode = client.fetchRssAsJson(rssUrl);
            List<ArticleSource> parsedArticles = TechBlogParser.parse(jsonNode);
            allArticles.addAll(parsedArticles);
        }

        return allArticles;
    }

    @Override
    public String getProviderName() {
        return "TechBlog";
    }
}