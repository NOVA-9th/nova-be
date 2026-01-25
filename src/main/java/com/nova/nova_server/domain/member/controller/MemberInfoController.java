package com.nova.nova_server.domain.member.controller;

import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.dto.MemberUpdateResponseDto;
import com.nova.nova_server.domain.member.service.MemberService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberInfoController {

    private final MemberService memberService;

    //사용자 정보 조회
    @GetMapping("/{member_id}")
    public ApiResponse<MemberResponseDto> getMemberInfo(
            @PathVariable("member_id") Long memberId) {

        MemberResponseDto response = memberService.getMemberInfo(memberId);
        return ApiResponse.success(response);
    }

    //사용자 정보 수정(이름만 수정 가능)
    @PatchMapping("/{member_id}")
    public ApiResponse<MemberUpdateResponseDto> updateMemberName(
            @PathVariable("member_id") Long memberId,
            @AuthenticationPrincipal Long authenticatedMemberId,
            @RequestBody MemberRequestDto requestDto) {

        MemberUpdateResponseDto response = memberService.updateMemberName(memberId, authenticatedMemberId, requestDto);
        return ApiResponse.success(response);
    }
}