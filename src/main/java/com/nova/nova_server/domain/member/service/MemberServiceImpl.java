package com.nova.nova_server.domain.member.service;

import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.dto.MemberUpdateResponseDto;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 프로필 이미지가 없으면 null, 있으면 이미지를 다운로드할 수 있는 URL 경로 반환
        // 예: /api/members/1/profile-image
        String profileImageUrl = (member.getProfileImage() != null)
                ? "/api/members/" + member.getId() + "/profile-image"
                : null;

        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .profileImage(profileImageUrl)
                .build();
    }

    @Override
    public MemberUpdateResponseDto updateMemberName(Long memberId, Long authenticatedMemberId, MemberRequestDto requestDto) {

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 이름 수정
        member.updateName(requestDto.getName());

        // 응답 DTO 생성
        return MemberUpdateResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .build();
    }
}