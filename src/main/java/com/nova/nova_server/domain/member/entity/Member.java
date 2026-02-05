package com.nova.nova_server.domain.member.entity;

import com.nova.nova_server.domain.auth.error.AuthErrorCode;
import com.nova.nova_server.domain.cardNews.entity.CardNewsBookmark;
import com.nova.nova_server.domain.cardNews.entity.CardNewsRelevance;
import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.global.apiPayload.exception.NovaException;
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

    @Column(nullable = true, unique = false, length = 255)
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

    public void updateProfileImage(byte[] newImage) {
        if (newImage == null) {
            this.profileImage = null;
            return;
        }

        if (this.profileImage == null) {
            this.profileImage = MemberProfileImage.builder()
                    .member(this)
                    .image(newImage)
                    .build();
            return;
        }

        this.profileImage.updateImage(newImage);
    }

    public void connectGoogle(String googleId) {
        this.googleId = googleId;
    }

    public void disconnectGoogle() {
        ensureDisconnectAvailable();
        this.googleId = null;
    }

    public void connectKakao(String kakaoId) {
        this.kakaoId = kakaoId;
    }

    public void disconnectKakao() {
        ensureDisconnectAvailable();
        this.kakaoId = null;
    }

    public void connectGithub(String githubId) {
        this.githubId = githubId;
    }

    public void disconnectGithub() {
        ensureDisconnectAvailable();
        this.githubId = null;
    }

    private void ensureDisconnectAvailable() {
        int connectedAccountCount = 0;

        if (this.googleId != null) {
            connectedAccountCount++;
        }

        if (this.kakaoId != null) {
            connectedAccountCount++;
        }

        if (this.githubId != null) {
            connectedAccountCount++;
        }

        if (connectedAccountCount <= 1) {
            throw new NovaException(AuthErrorCode.NO_REMAINING_ACCOUNT);
        }
    }
}
