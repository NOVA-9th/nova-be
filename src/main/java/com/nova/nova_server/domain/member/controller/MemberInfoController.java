package com.nova.nova_server.domain.member.controller;

import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.dto.MemberUpdateResponseDto;
import com.nova.nova_server.domain.member.service.MemberService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * [POST] 프로필 이미지 업로드 (생성)
     */
    @PostMapping(value = "/{memberId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> uploadProfileImage(
            @PathVariable Long memberId,
            @RequestParam("file") MultipartFile file) {
        return processImageUpload(memberId, file);
    }

    /**
     * [PUT] 프로필 이미지 수정
     * 설명: 기존 이미지를 덮어쓰므로 로직은 POST와 동일하지만, RESTful한 설계를 위해 PUT 메서드 제공
     */
    @PutMapping(value = "/{memberId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateProfileImage(
            @PathVariable Long memberId,
            @RequestParam("file") MultipartFile file) {
        return processImageUpload(memberId, file);
    }

    // (POST와 PUT의 중복 로직을 처리하는 헬퍼 메서드)
    private ResponseEntity<byte[]> processImageUpload(Long memberId, MultipartFile file) {
        try {
            byte[] imageBytes = memberService.uploadProfileImage(memberId, file);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * [GET] 프로필 이미지 조회
     */
    @GetMapping("/{memberId}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long memberId) {
        try {
            byte[] imageBytes = memberService.getProfileImage(memberId);
            if (imageBytes == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * [DELETE] 프로필 이미지 삭제
     * 설명: DB의 이미지 데이터를 null로 변경
     */
    @DeleteMapping("/{memberId}/profile-image")
    public ResponseEntity<Void> deleteProfileImage(@PathVariable Long memberId) {
        try {
            memberService.deleteProfileImage(memberId);
            // 삭제 성공 시 내용 없음(204 No Content) 반환이 일반적
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}