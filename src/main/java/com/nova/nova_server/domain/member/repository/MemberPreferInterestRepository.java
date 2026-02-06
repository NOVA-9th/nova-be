package com.nova.nova_server.domain.member.repository;

import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.entity.MemberPreferInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPreferInterestRepository extends JpaRepository<MemberPreferInterest, Long> {
    long deleteByMember(Member member);
}
