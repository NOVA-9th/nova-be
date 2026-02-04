package com.nova.nova_server.domain.member.controller;

import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.dto.MemberUpdateResponseDto;
import com.nova.nova_server.domain.member.service.MemberService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberInfoController {

    private final MemberService memberService;

    // 사용자 정보 조회
    @GetMapping("/{member_id}")
    public ApiResponse<MemberResponseDto> getMemberInfo(
            @PathVariable("member_id") Long memberId) {
        MemberResponseDto response = memberService.getMemberInfo(memberId);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{member_id}")
    public ApiResponse<Void> deleteMember(
        @PathVariable("member_id") Long memberId,
        @AuthenticationPrincipal Long authenticatedMemberId
    ) {
        memberService.deleteMember(memberId);
        return ApiResponse.success(null);
    }

    // 사용자 정보 수정(이름만 수정 가능)
    @PatchMapping("/{member_id}")
    public ApiResponse<MemberUpdateResponseDto> updateMemberName(
            @PathVariable("member_id") Long memberId,
            @AuthenticationPrincipal Long authenticatedMemberId,
            @RequestBody MemberRequestDto requestDto) {
        MemberUpdateResponseDto response = memberService.updateMemberName(memberId, authenticatedMemberId, requestDto);
        return ApiResponse.success(response);
    }

    // 프로필 이미지 업로드 (수정도 가능)
    @PostMapping(value = "/{memberId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> uploadProfileImage(
            @PathVariable Long memberId,
            @RequestParam("file") MultipartFile file) throws IOException {

        byte[] imageBytes = memberService.uploadProfileImageRaw(memberId, file);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

    // 프로필 이미지 조회
    @GetMapping("/{memberId}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long memberId) {
        byte[] imageBytes = memberService.getProfileImageRaw(memberId);

        // 이미지가 없는 경우 바디 없이 404 상태 코드만 반환
        if (imageBytes == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/{memberId}/profile-image")
    public ApiResponse<Object> deleteProfileImage(@PathVariable Long memberId) {
        memberService.deleteProfileImage(memberId);
        //ApiResponse.success(null) 반환
        return ApiResponse.success(null);
    }
}