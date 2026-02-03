package com.nova.nova_server.domain.cardNews.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "card_news")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardNews extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private CardType cardType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 100)
    private String author;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String evidence;

    @Column(name = "original_url", length = 2048)
    private String originalUrl;

    @Column(name = "source_site_name", length = 100)
    private String sourceSiteName;

    @OneToMany(mappedBy = "cardNews", fetch = FetchType.LAZY)
    private List<CardNewsBookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "cardNews", fetch = FetchType.LAZY)
    private List<CardNewsRelevance> relevances = new ArrayList<>();

    @OneToMany(mappedBy = "cardNews", fetch = FetchType.LAZY)
    private List<CardNewsKeyword> keywords = new ArrayList<>();
}
