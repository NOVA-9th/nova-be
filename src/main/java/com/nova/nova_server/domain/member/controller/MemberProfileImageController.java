package com.nova.nova_server.domain.member.controller;

import com.nova.nova_server.domain.member.service.MemberProfileImageService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member Profile Image", description = "사용자 프로필 이미지 관련 API")
public class MemberProfileImageController {

    private final MemberProfileImageService memberProfileImageService;

    @Operation(
            summary = "프로필 이미지 업로드 및 수정",
            description = "사용자의 프로필 이미지를 업로드합니다. 이미 이미지가 존재하는 경우 새 이미지로 교체됩니다."
    )
    @PostMapping(value = "/{memberId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> uploadProfileImage(
            @Parameter(description = "이미지를 업로드할 사용자의 ID", example = "1")
            @PathVariable Long memberId,
            @Parameter(
                    description = "업로드할 이미지 파일 (MultipartFile)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) throws IOException {

        memberProfileImageService.uploadProfileImageRaw(memberId, file);
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "프로필 이미지 조회",
            description = "사용자의 프로필 이미지를 바이트 배열 형태로 직접 조회합니다."
    )
    @GetMapping("/{memberId}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(
            @Parameter(description = "이미지를 조회할 사용자의 ID", example = "1")
            @PathVariable Long memberId) {

        Optional<byte[]> imageBytes = memberProfileImageService.getProfileImageRaw(memberId);

        return imageBytes
                .map(this::createJpegResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "프로필 이미지 삭제",
            description = "사용자의 프로필 이미지를 서버에서 삭제합니다."
    )
    @DeleteMapping("/{memberId}/profile-image")
    public ApiResponse<Void> deleteProfileImage(
            @Parameter(description = "이미지를 삭제할 사용자의 ID", example = "1")
            @PathVariable Long memberId) {

        memberProfileImageService.deleteProfileImage(memberId);
        return ApiResponse.success(null);
    }

    private ResponseEntity<byte[]> createJpegResponse(byte[] jpeg) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(jpeg);
    }
}