package com.nova.nova_server.domain.member.repository;

import com.nova.nova_server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
