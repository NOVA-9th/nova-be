package com.nova.nova_server.domain.member.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import com.nova.nova_server.domain.interest.entity.Interest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "member_prefer_interest",
    uniqueConstraints = @UniqueConstraint(name = "UK_mpi_member_interest", columnNames = {"member_id", "interest_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MemberPreferInterest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "FK_member_TO_mpi"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false, foreignKey = @ForeignKey(name = "FK_interest_TO_mpi"))
    private Interest interest;
}
