package com.nova.nova_server.domain.batch.articleingestion.service;

import com.nova.nova_server.domain.batch.common.repository.ArticleEntityRepository;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// ArticleApiService 로부터 글 목록들을 가져오는 ItemReader
@Slf4j
@RequiredArgsConstructor
public class ArticleSourceItemReader implements ItemStreamReader<ArticleSource> {

    private final ArticleApiService articleApiService;
    private final ArticleEntityRepository articleEntityRepository;

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
            Map<String, ArticleSource> articleUrlMap = articleApiService.fetchArticles()
                    .stream()
                    .collect(Collectors.toMap(
                            ArticleSource::getUrl,
                            articleSource -> articleSource,
                            (existing, replacement) -> existing));

            sources = findNonExistingUrls(articleUrlMap.keySet())
                    .stream()
                    .map(articleUrlMap::get)
                    .toList();

            log.info("Loaded {} articles from {}", sources.size(), articleApiService.getProviderName());
        }

        if (index >= sources.size()) {
            return null; // 데이터 끝
        }

        return sources.get(index++);
    }

    private Set<String> findNonExistingUrls(Set<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return Set.of();
        }
        Set<String> existingUrls = articleEntityRepository.findUrlsByUrlIn(urls);
        return urls.stream()
                .filter(url -> !existingUrls.contains(url))
                .collect(Collectors.toSet());
    }

    @Override
    public void update(@NonNull ExecutionContext executionContext) {}
    @Override
    public void close() {}
}