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

import com.nova.nova_server.global.apiPayload.code.success.CommonSuccessCode;

import java.util.HashMap;
import java.util.Map;

import java.util.Map;

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

    //프로필 이미지 업로드
    @PostMapping(value = "/{memberId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> uploadProfileImage(
            @PathVariable Long memberId,
            @RequestParam("file") MultipartFile file) {
        return processImageUpload(memberId, file);
    }

    //프로필 이미지 수정
    @PutMapping(value = "/{memberId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateProfileImage(
            @PathVariable Long memberId,
            @RequestParam("file") MultipartFile file) {
        return processImageUpload(memberId, file);
    }

    // 업로드/수정 전용 헬퍼: 바이너리(byte[]) 형태로 응답
    private ResponseEntity<byte[]> processImageUpload(Long memberId, MultipartFile file) {
        try {
            // Service에서 압축 후 저장된 byte[]를 그대로 가져옴
            byte[] imageBytes = memberService.uploadProfileImageRaw(memberId, file);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //프로필 이미지 조회
    @GetMapping("/{memberId}/profile-image")
    public ResponseEntity<?> getProfileImage(@PathVariable Long memberId) {
        byte[] imageBytes = memberService.getProfileImageRaw(memberId);

        // 이미지가 없는 경우 (삭제 후 등) -> JSON 응답 (profileImage: null)
        if (imageBytes == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", memberId);
            data.put("profileImage", null);

            return ResponseEntity.ok(ApiResponse.success(data));
        }

        //이미지가 있는 경우 -> 바이너리 이미지 파일 응답
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

    //프로필 이미지 삭제
    @DeleteMapping("/{memberId}/profile-image")
    public ApiResponse<Map<String, Object>> deleteProfileImage(@PathVariable Long memberId) {
        memberService.deleteProfileImage(memberId);

        Map<String, Object> data = new HashMap<>();
        data.put("id", memberId);
        data.put("profileImage", null);

        return ApiResponse.success(data);
    }

}