package com.nova.nova_server.domain.auth.controller;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.service.GoogleOAuthService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor
public class GoogleOAuthController {

	private final GoogleOAuthService googleOAuthService;

	@GetMapping
	public ResponseEntity<Void> redirectToGoogle() {
		String authorizationUrl = googleOAuthService.getAuthorizationUrl();
		return ResponseEntity.status(302)
			.header("Location", authorizationUrl)
			.build();
	}

	@GetMapping("/callback")
	public ApiResponse<AuthResponse> callback(@RequestParam("code") String code) {
		AuthResponse authResponse = googleOAuthService.handleCallback(code);
		return ApiResponse.success(authResponse);
	}
}
