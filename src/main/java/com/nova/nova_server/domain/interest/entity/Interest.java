package com.nova.nova_server.domain.interest.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.keyword.entity.Keyword;
import com.nova.nova_server.domain.member.entity.MemberPreferInterest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interest")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "interest", fetch = FetchType.LAZY)
    private List<Keyword> keywords = new ArrayList<>();

    @OneToMany(mappedBy = "interest", fetch = FetchType.LAZY)
    private List<MemberPreferInterest> memberPreferInterests = new ArrayList<>();
}
