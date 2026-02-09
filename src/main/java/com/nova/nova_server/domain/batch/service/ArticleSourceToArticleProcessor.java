package com.nova.nova_server.domain.batch.service;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Spring Batch ItemProcessor: ArticleSource → Article (fetchArticle).
 */
@Slf4j
@Component
public class ArticleSourceToArticleProcessor implements ItemProcessor<ArticleSource, Article> {

    @Override
    public Article process(ArticleSource source) {
        try {
            Article article = source.fetchArticle();
            return article;
        } catch (Exception e) {
            log.warn("Processor failed for url={}: {}", source.getUrl(), e.getMessage());
            return null; // skip this item
        }
    }
}
