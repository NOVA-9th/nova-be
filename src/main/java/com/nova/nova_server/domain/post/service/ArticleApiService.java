package com.nova.nova_server.domain.post.service;

import com.nova.nova_server.domain.post.model.Article;
import java.util.List;

/**
 * 외부 뉴스 API로부터 기사를 가져오는 공통 인터페이스
 */
public interface ArticleApiService {

    /**
     * 기사 목록 조회
     * @return 기사 목록
     */
    List<Article> fetchArticles();

    /**
     * API 제공자 이름 반환
     * @return API 이름 (예: "NewsAPI", "NaverNews", "DeepSearch", "NewsData")
     */
    String getProviderName();
}