package com.nova.nova_server.domain.auth.controller;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.service.GoogleOAuthService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor
public class GoogleOAuthController {

	private final GoogleOAuthService googleOAuthService;

	@GetMapping
	public ResponseEntity<Void> redirectToGoogle(
			@RequestParam(value = "state", required = false) String state
	) {
		String authorizationUrl = googleOAuthService.getAuthorizationUrl(state);
		return ResponseEntity.status(302)
			.header("Location", authorizationUrl)
			.build();
	}

	@GetMapping("/callback")
	public ApiResponse<AuthResponse> callback(@RequestParam("code") String code) {
		AuthResponse authResponse = googleOAuthService.handleCallback(code);
		return ApiResponse.success(authResponse);
	}

	@PostMapping("/connect")
	public ApiResponse<Void> connect(
			@AuthenticationPrincipal Long memberId,
			@RequestParam("code") String code
	) {
		googleOAuthService.connect(memberId, code);
		return ApiResponse.success(null);
	}

	@PostMapping("/disconnect")
	public ApiResponse<Void> connect(
			@AuthenticationPrincipal Long memberId
	) {
		googleOAuthService.disconnect(memberId);
		return ApiResponse.success(null);
	}
}
