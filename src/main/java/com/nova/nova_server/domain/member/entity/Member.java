package com.nova.nova_server.domain.member.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "google_id", length = 255, nullable = true)
    private String googleId;

    @Column(name = "kakao_id", length = 255, nullable = true)
    private String kakaoId;

    @Column(name = "github_id", length = 255, nullable = true)
    private String githubId;

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
}
