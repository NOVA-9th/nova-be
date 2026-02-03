package com.nova.nova_server.domain.auth.controller;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.service.KakaoOAuthService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoOAuthController {

	private final KakaoOAuthService kakaoOAuthService;

	@GetMapping
	public ResponseEntity<Void> redirectToKakao() {
		String authorizationUrl = kakaoOAuthService.getAuthorizationUrl();
		return ResponseEntity.status(302)
			.header("Location", authorizationUrl)
			.build();
	}

	@GetMapping("/callback")
	public ApiResponse<AuthResponse> callback(@RequestParam("code") String code) {
		AuthResponse authResponse = kakaoOAuthService.handleCallback(code);
		return ApiResponse.success(authResponse);
	}

	@PostMapping("/connect")
	public ApiResponse<Void> connect(
			@AuthenticationPrincipal Long memberId,
			@RequestParam("code") String code
	) {
		kakaoOAuthService.connect(memberId, code);
		return ApiResponse.success(null);
	}

	@PostMapping("/disconnect")
	public ApiResponse<Void> disconnect(
			@AuthenticationPrincipal Long memberId
	) {
		kakaoOAuthService.disconnect(memberId);
		return ApiResponse.success(null);
	}
}
