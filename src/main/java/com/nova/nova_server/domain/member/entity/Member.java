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

    @Column(length = 255, nullable = true)
    private String background;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MemberLevel level;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberProfileImage profileImage;

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

    // Entity
    public void updateName(String name) {
        this.name = name;
    }

    public void updateProfileImage(byte[] profileImage) {
        if (profileImage == null) {
            this.profileImage = null;
            return;
        }

        if (this.profileImage == null) {
            this.profileImage = MemberProfileImage.builder()
                    .member(this)
                    .image(profileImage)
                    .build();
            return;
        }

        this.profileImage.updateImage(profileImage);
    }
}
