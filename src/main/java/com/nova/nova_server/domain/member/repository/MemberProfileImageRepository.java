package com.nova.nova_server.domain.member.repository;

import com.nova.nova_server.domain.member.entity.MemberProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileImageRepository extends JpaRepository<MemberProfileImage, Long> {
}
