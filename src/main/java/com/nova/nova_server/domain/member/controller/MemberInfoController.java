package com.nova.nova_server.domain.member.controller;

import com.nova.nova_server.domain.member.dto.*;
import com.nova.nova_server.domain.member.error.MemberErrorCode;
import com.nova.nova_server.domain.member.service.MemberService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import com.nova.nova_server.global.apiPayload.exception.NovaException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "사용자 정보 및 설정 관련 API")
public class MemberInfoController {

    private final MemberService memberService;

    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 기본 프로필 정보를 조회합니다.")
    @GetMapping("/{member_id}")
    public ApiResponse<MemberResponseDto> getMemberInfo(
            @Parameter(description = "조회할 사용자의 ID", example = "1")
            @PathVariable("member_id") Long memberId) {

        MemberResponseDto response = memberService.getMemberInfo(memberId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다.")
    @DeleteMapping("/{member_id}")
    public ApiResponse<Void> deleteMember(
            @Parameter(description = "삭제할 사용자의 ID")
            @PathVariable("member_id") Long memberId,
            @AuthenticationPrincipal Long authenticatedMemberId) {

        memberService.deleteMember(memberId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "사용자 이름 수정", description = "사용자의 이름을 수정합니다. (현재 이름만 수정 가능)")
    @PatchMapping("/{member_id}")
    public ApiResponse<MemberUpdateResponseDto> updateMemberName(
            @Parameter(description = "수정할 사용자의 ID")
            @PathVariable("member_id") Long memberId,
            @AuthenticationPrincipal Long authenticatedMemberId,
            @RequestBody MemberRequestDto requestDto) {

        MemberUpdateResponseDto response = memberService.updateMemberName(memberId, authenticatedMemberId, requestDto);
        return ApiResponse.success(response);
    }

    @Operation(summary = "개인화 설정 조회", description = "관심 키워드 등 사용자의 개인화 설정 정보를 조회합니다.")
    @GetMapping("/{memberId}/personalization")
    public ApiResponse<MemberPersonalizationDto> getPersonalization(
            @Parameter(description = "사용자 ID")
            @PathVariable Long memberId) {

        MemberPersonalizationDto personalization = memberService.getMemberPersonalization(memberId);
        return ApiResponse.success(personalization);
    }

    @Operation(summary = "개인화 설정 수정", description = "사용자의 관심사나 개인화 설정 정보를 업데이트합니다.")
    @PutMapping("/{memberId}/personalization")
    public ApiResponse<Void> updatePersonalization(
            @Parameter(description = "사용자 ID")
            @PathVariable Long memberId,
            @RequestBody MemberPersonalizationDto request) {

        memberService.updateMemberPersonalization(memberId, request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "관심 키워드 조회", description = "사용자의 관심 키워드 목록을 조회합니다.")
    @GetMapping("/{member_id}/keywords")
    public ApiResponse<MemberPreferKeywordResponseDto> getMemberKeywords(
            @Parameter(description = "사용자 ID")
            @PathVariable("member_id") Long memberId,
            @AuthenticationPrincipal Long authenticatedMemberId
    ) {
        if (!authenticatedMemberId.equals(memberId)) {
            throw new NovaException(MemberErrorCode.MEMBER_READ_FORBIDDEN);
        }

        List<String> keywords = memberService.getMemberKeywords(memberId);
        return ApiResponse.success(MemberPreferKeywordResponseDto.builder()
                .totalCount(keywords.size())
                .keywords(keywords)
                .build()
        );
    }

    @Operation(summary = "연결된 계정 조회", description = "소셜 로그인 등 사용자와 연결된 계정 정보를 조회합니다.")
    @GetMapping("/{member_id}/connected-accounts")
    public ApiResponse<MemberConnectedAccountsResponseDto> getConnectedAccounts(
            @Parameter(description = "사용자 ID")
            @PathVariable("member_id") Long memberId) {

        MemberConnectedAccountsResponseDto response = memberService.getConnectedAccounts(memberId);
        return ApiResponse.success(response);
    }
}