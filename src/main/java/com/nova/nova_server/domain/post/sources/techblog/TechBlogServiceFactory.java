package com.nova.nova_server.domain.post.sources.techblog;

import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.service.ArticleApiServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TechBlogServiceFactory {
    // 수집할 기술 블로그 RSS URL 목록(추후 더 추가하면 좋을듯..?)
    private static final Map<String, String> BLOG_RSS_URLS = Map.of(
            "Woowa Bros", "https://techblog.woowahan.com/feed/",
            "Kakao", "https://tech.kakao.com/feed/",
            "Naver D2", "https://d2.naver.com/d2.atom",
            "Daangn", "https://medium.com/feed/daangn",
            "NHN Cloud", "https://meetup.nhncloud.com/rss",
            "Toss", "https://toss.tech/rss.xml"
    );

    private final TechBlogClient client;

    public List<ArticleApiService> createAllAvailableServices() {
        return BLOG_RSS_URLS.values().stream().map(this::createTechBlogService).toList();
    }

    public ArticleApiService createTechBlogService(String url) {
        return new TechBlogServiceImpl(client, url);
    }
}
