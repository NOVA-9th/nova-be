package com.nova.nova_server.domain.member.controller;

import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberInfoController {

    private final MemberRepository memberRepository;
    //사용자 조회
    @GetMapping("/{member_id}")
    public ApiResponse<MemberResponseDto> getMemberInfo(
            @PathVariable("member_id") Long memberId,
            @AuthenticationPrincipal Long authenticatedMemberId) {

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 프로필 이미지 Base64 인코딩
        String profileImageBase64 = encodeProfileImage(member.getProfileImage());

        // 응답 DTO 생성
        MemberResponseDto response = MemberResponseDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .profileImage(profileImageBase64)
                .build();

        return ApiResponse.success(response);
    }

    /**
     * 프로필 이미지를 Base64로 인코딩
     * - 이미지가 없으면 null 반환
     * - 이미지가 있으면 Base64 문자열 반환
     */
    private String encodeProfileImage(byte[] profileImage) {
        if (profileImage == null || profileImage.length == 0) {
            return null;
        }
        return Base64.getEncoder().encodeToString(profileImage);
    }
}