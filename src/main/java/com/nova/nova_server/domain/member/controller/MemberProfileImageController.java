package com.nova.nova_server.domain.member.controller;

import com.nova.nova_server.domain.member.service.MemberProfileImageService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberProfileImageController {

    private final MemberProfileImageService memberProfileImageService;

    // 프로필 이미지 업로드 (수정도 가능)
    @PostMapping(value = "/{memberId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> uploadProfileImage(
            @PathVariable Long memberId,
            @RequestParam("file") MultipartFile file) throws IOException {

        memberProfileImageService.uploadProfileImageRaw(memberId, file);

        return ApiResponse.success(null);
    }

    // 프로필 이미지 조회
    @GetMapping("/{memberId}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long memberId) {
        Optional<byte[]> imageBytes = memberProfileImageService.getProfileImageRaw(memberId);

        return imageBytes
                .map(bytes -> createJpegResponse(bytes))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<byte[]> createJpegResponse(byte[] jpeg) {
        return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(jpeg);
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/{memberId}/profile-image")
    public ApiResponse<Void> deleteProfileImage(@PathVariable Long memberId) {
        memberProfileImageService.deleteProfileImage(memberId);
        return ApiResponse.success(null);
    }
}
