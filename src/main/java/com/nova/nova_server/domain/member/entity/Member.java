package com.nova.nova_server.domain.member.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberLevel level = MemberLevel.BASIC;

    @Column(length = 255)
    private String background;

    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(length = 20)
    private String phone;

    @Column(name = "sns_id", length = 255)
    private String snsId;

    @Column(name = "extra_info", length = 255)
    private String extraInfo;

    public enum MemberLevel {
        BASIC, VIP, ADMIN
    }
}
