package com.nova.nova_server.domain.cardNews.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "card_news_hidden",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_hidden_member_card_news",
                columnNames = {"member_id", "card_news_id"}
        )
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CardNewsHidden extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_member_TO_cnh")
    )
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "card_news_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_card_news_TO_cnh")
    )
    private CardNews cardNews;

}
