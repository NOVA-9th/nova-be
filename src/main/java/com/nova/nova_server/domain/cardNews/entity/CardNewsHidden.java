package com.nova.nova_server.domain.cardNews.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "card_news_hidden",
        uniqueConstraints = @UniqueConstraint(
                name = CardNewsHidden.UNIQUE_CONSTRAINT_NAME,
                columnNames = {"member_id", "card_news_id"}
        )
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CardNewsHidden extends BaseEntity {

    public static final String UNIQUE_CONSTRAINT_NAME = "UK_hidden_member_card_news";
    public static final String FK_NAME_MEMBER = "FK_member_TO_cnh";
    public static final String FK_NAME_CARD_NEWS = "FK_card_news_TO_cnh";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id", nullable = false,
            foreignKey = @ForeignKey(name = FK_NAME_MEMBER)
    )
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "card_news_id", nullable = false,
            foreignKey = @ForeignKey(name = FK_NAME_CARD_NEWS)
    )
    private CardNews cardNews;

}
