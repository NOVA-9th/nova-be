package com.nova.nova_server.domain.batch.common.entity;

import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.post.model.Article;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "article", indexes = @Index(name = "idx_article_url", columnList = "url", unique = true))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String content;

    @Column(length = 100, nullable = true)
    private String author;

    @Column(length = 200, nullable = true)
    private String source;

    @Column(name = "published_at", nullable = true)
    private LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = true)
    private CardType cardType;

    @Column(nullable = false, length = 2048)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Builder.Default
    @Setter
    private ArticleState state = ArticleState.STAGED;

    @Column(nullable = true)
    @Setter
    private String batchId;
}
