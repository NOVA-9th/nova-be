package com.nova.nova_server.domain.batch.common.converter;

import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import com.nova.nova_server.domain.batch.common.entity.ArticleState;
import com.nova.nova_server.domain.post.model.Article;

public class ArticleConverter {
    private static final int TEXT_MAX_LENGTH = 10_000;

    public static Article toDomain(ArticleEntity entity) {
        return Article.builder()
                .title(entity.getTitle())
                .content(entity.getContent())
                .url(entity.getUrl())
                .author(entity.getAuthor())
                .source(entity.getSource())
                .publishedAt(entity.getPublishedAt())
                .cardType(entity.getCardType())
                .build();
    }

    public static ArticleEntity toEntity(Article article, String sourceUrl) {
        String content = article.content();
        if (content != null && content.length() > TEXT_MAX_LENGTH) {
            content = content.substring(0, TEXT_MAX_LENGTH);
        }
        return ArticleEntity.builder()
                .title(article.title())
                .content(content)
                .author(article.author())
                .source(article.source())
                .state(ArticleState.STAGED)
                .publishedAt(article.publishedAt())
                .cardType(article.cardType())
                .url(article.url())
                .sourceUrl(sourceUrl)
                .build();
    }

    public static ArticleEntity toFailedEntity(String sourceUrl) {
        return ArticleEntity.builder()
                .title("")
                .content(null)
                .author(null)
                .source(null)
                .state(ArticleState.FAILED)
                .publishedAt(null)
                .cardType(null)
                .url("")
                .sourceUrl(sourceUrl)
                .build();
    }
}
