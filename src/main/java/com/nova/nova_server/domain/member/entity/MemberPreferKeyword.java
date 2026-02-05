package com.nova.nova_server.domain.member.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.keyword.entity.Keyword;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "member_prefer_keyword",
    uniqueConstraints = @UniqueConstraint(name = "UK_mpk_member_keyword", columnNames = {"member_id", "keyword_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MemberPreferKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "FK_member_TO_mpk"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false, foreignKey = @ForeignKey(name = "FK_keyword_TO_mpk"))
    private Keyword keyword;
}
