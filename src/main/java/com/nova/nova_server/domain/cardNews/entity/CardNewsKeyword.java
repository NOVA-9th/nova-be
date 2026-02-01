package com.nova.nova_server.domain.cardNews.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.keyword.entity.Keyword;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "card_news_keyword", uniqueConstraints = @UniqueConstraint(name = "UK_cnk_card_news_keyword", columnNames = {
        "card_news_id", "keyword_id" }))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardNewsKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_news_id", nullable = false)
    private Long cardNewsId;

    @Column(name = "keyword_id", nullable = false)
    private Long keywordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_news_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_card_news_TO_cnk"))
    private CardNews cardNews;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_keyword_TO_cnk"))
    private Keyword keyword;
}
