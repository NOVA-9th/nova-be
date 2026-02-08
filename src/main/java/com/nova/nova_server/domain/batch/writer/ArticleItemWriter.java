package com.nova.nova_server.domain.batch.writer;

import com.nova.nova_server.domain.batch.entity.ArticleEntity;
import com.nova.nova_server.domain.batch.repository.ArticleEntityRepository;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Spring Batch ItemWriter: Article → DB (ArticleEntity). Skips items with empty or duplicate url.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleItemWriter implements ItemWriter<Article> {

    private final ArticleEntityRepository articleEntityRepository;

    @Override
    public void write(Chunk<? extends Article> chunk) {
        for (Article article : chunk.getItems()) {
            try {
                if (article.url() == null || article.url().isBlank()) {
                    log.debug("Skipping Article with empty url: title={}", article.title());
                    continue;
                }
                if (articleEntityRepository.existsByUrl(article.url())) {
                    log.debug("Skipping duplicate url: {}", article.url());
                    continue;
                }
                ArticleEntity entity = toEntity(article);
                articleEntityRepository.save(entity);
            } catch (Exception e) {
                log.warn("Writer failed for title={}: {}", article.title(), e.getMessage());
            }
        }
    }

    private static ArticleEntity toEntity(Article article) {
        return ArticleEntity.builder()
                .title(article.title() != null ? article.title() : "")
                .content(article.content())
                .author(article.author())
                .source(article.source())
                .publishedAt(article.publishedAt())
                .cardType(article.cardType())
                .url(article.url())
                .build();
    }
}
