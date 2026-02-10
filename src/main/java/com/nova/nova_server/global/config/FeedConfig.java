package com.nova.nova_server.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class FeedConfig {

    @Value("${feed.max-page-size:10}")
    private int maxPageSize;

    /**
     * 피드 최대 점수<br/>
     * - 기본 점수: 50<br/>
     * - 키워드 매칭 시: +10<br/>
     * - 북마크된 카드 뉴스와 키워드 매칭 시: +5<br/>
     * 카드 뉴스 하나 당 키워드는 최대 5개이므로 최대 점수는 125점
     */
    @Value("${feed.max-score:125}")
    private int maxScore;

    @Value("${feed.base-score:50}")
    private int baseScore;

    @Value("${feed.keyword-match-score:10}")
    private int keywordMatchScore;

    @Value("${feed.bookmark-keyword-match-score:5}")
    private int bookmarkKeywordMatchScore;

}
