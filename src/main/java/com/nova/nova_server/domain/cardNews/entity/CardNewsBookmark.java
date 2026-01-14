package com.nova.nova_server.domain.cardNews.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "card_news_bookmark",
    uniqueConstraints = @UniqueConstraint(name = "UK_bookmark_member_card_news", columnNames = {"member_id", "card_news_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardNewsBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "card_news_id", nullable = false)
    private Long cardNewsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_member_TO_bookmark"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_news_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_card_news_TO_bookmark"))
    private CardNews cardNews;
}
