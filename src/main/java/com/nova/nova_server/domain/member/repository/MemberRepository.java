package com.nova.nova_server.domain.member.repository;

import com.nova.nova_server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);
	Optional<Member> findByGoogleId(String googleId);
	boolean existsByGoogleId(String googleId);
	Optional<Member> findByKakaoId(String kakaoId);
	boolean existsByKakaoId(String kakaoId);
	Optional<Member> findByGithubId(String githubId);
	boolean existsByGithubId(String githubId);
	boolean existsByRole(Member.MemberRole role);
	Optional<Member> findByRole(Member.MemberRole role);
}
