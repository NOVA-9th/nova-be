package com.nova.nova_server.domain.keyword.entity;

import com.nova.nova_server.domain.cardNews.entity.CardNewsKeyword;
import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.interest.entity.Interest;
import com.nova.nova_server.domain.member.entity.MemberPreferKeyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false, foreignKey = @ForeignKey(name = "FK_interest_TO_keyword"))
    private Interest interest;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<KeywordStatistics> statistics = new ArrayList<>();

    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<MemberPreferKeyword> memberPreferKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<CardNewsKeyword> cardNewsKeywords = new ArrayList<>();
}
