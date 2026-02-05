package com.nova.nova_server.domain.member.repository;

import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.entity.MemberPreferKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberPreferKeywordRepository extends JpaRepository<MemberPreferKeyword, Long> {
    @Query("""
            select k.name
            from MemberPreferKeyword mpk
            join mpk.keyword k
            where mpk.member.id = :memberId
            """)
    List<String> findKeywordNamesByMemberId(@Param("memberId") Long memberId);

    long deleteByMember(Member member);
}
