package com.nova.nova_server.domain.batch.articleingestion.service;

import com.nova.nova_server.domain.batch.common.converter.ArticleConverter;
import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArticleFetchProcessor implements ItemProcessor<ArticleSource, ArticleEntity> {
    @Override
    public ArticleEntity process(@NonNull ArticleSource source) {
        try {
            Article article = source.fetchArticle();
            if (article == null) {
                return null;
            }
            return ArticleConverter.toEntity(article, source.getUrl());
        } catch (Exception e) {
            log.error("Processor failed for url={}", source.getUrl(), e);
            return ArticleConverter.toFailedEntity(source.getUrl());
        }
    }
}
