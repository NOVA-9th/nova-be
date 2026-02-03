package com.nova.nova_server.domain.member.entity;

import com.nova.nova_server.domain.cardNews.entity.CardNewsBookmark;
import com.nova.nova_server.domain.cardNews.entity.CardNewsRelevance;
import com.nova.nova_server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(length = 255, nullable = true)
    private String background;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MemberLevel level;

    @Setter
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Setter
    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    @Column(name = "google_id", length = 255, nullable = true)
    private String googleId;

    @Column(name = "kakao_id", length = 255, nullable = true)
    private String kakaoId;

    @Column(name = "github_id", length = 255, nullable = true)
    private String githubId;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemberPreferKeyword> preferKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemberPreferInterest> preferInterests = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CardNewsBookmark> cardNewsBookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CardNewsRelevance> cardNewsRelevances = new ArrayList<>();

    public enum MemberLevel {
        NOVICE, // 입문자
        BEGINNER, // 초급자
        INTERMEDIATE, // 중급자
        ADVANCED // 숙련자
    }
}
