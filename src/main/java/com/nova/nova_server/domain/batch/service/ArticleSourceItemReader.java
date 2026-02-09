package com.nova.nova_server.domain.batch.service;

import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ArticleSourceItemReader implements ItemStreamReader<ArticleSource> {

    private final ArticleApiService articleApiService;

    private List<ArticleSource> sources;
    private int index = 0;

    @Override
    public void open(@NonNull ExecutionContext executionContext) {
        this.index = 0;
        this.sources = null;
        log.info("ArticleSourceItemReader initialized for provider: {}", articleApiService.getProviderName());
    }

    @Override
    public ArticleSource read() {
        if (sources == null) {
            sources = articleApiService.fetchArticles();
            log.info("Loaded {} articles from {}", sources.size(), articleApiService.getProviderName());
        }

        if (index >= sources.size()) {
            return null; // 데이터 끝
        }

        return sources.get(index++);
    }

    // ItemStream 인터페이스의 나머지 메서드들 (구현 안 해도 되지만 명시적으로)
    @Override
    public void update(@NonNull ExecutionContext executionContext) {}
    @Override
    public void close() {}
}