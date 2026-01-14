package com.nova.nova_server.domain.keyword.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.interest.entity.Interest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
