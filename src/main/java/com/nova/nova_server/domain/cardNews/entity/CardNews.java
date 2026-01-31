package com.nova.nova_server.domain.cardNews.entity;

import com.nova.nova_server.domain.cardType.entity.CardType;
import com.nova.nova_server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "card_news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardNews extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_type_id", nullable = false, foreignKey = @ForeignKey(name = "FK_card_type_TO_card_news"))
    private CardType cardType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 100)
    private String author;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(length = 255)
    private String evidence;

    @Column(name = "original_url", length = 2048)
    private String originalUrl;

    @Column(name = "source_site_name", length = 100)
    private String sourceSiteName;

    @Builder
    public CardNews(CardType cardType, String title, String author, LocalDateTime publishedAt,
                    String summary, String evidence, String originalUrl, String sourceSiteName) {
        this.cardType = cardType;
        this.title = title;
        this.author = author;
        this.publishedAt = publishedAt;
        this.summary = summary;
        this.evidence = evidence;
        this.originalUrl = originalUrl;
        this.sourceSiteName = sourceSiteName;
    }
}
