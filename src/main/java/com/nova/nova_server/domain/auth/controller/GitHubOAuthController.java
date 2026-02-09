package com.nova.nova_server.domain.auth.controller;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.service.GitHubOAuthService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/github")
@RequiredArgsConstructor
public class GitHubOAuthController {

	private final GitHubOAuthService gitHubOAuthService;

	@GetMapping
	public ResponseEntity<Void> redirectToGitHub(
			@RequestParam(value = "state", required = false) String state
	) {
		String authorizationUrl = gitHubOAuthService.getAuthorizationUrl(state);
		return ResponseEntity.status(302)
			.header("Location", authorizationUrl)
			.build();
	}

	@GetMapping("/callback")
	public ApiResponse<AuthResponse> callback(@RequestParam("code") String code) {
		AuthResponse authResponse = gitHubOAuthService.handleCallback(code);
		return ApiResponse.success(authResponse);
	}

	@PostMapping("/connect")
	public ApiResponse<Void> connect(
			@AuthenticationPrincipal Long memberId,
			@RequestParam("code") String code
	) {
		gitHubOAuthService.connect(memberId, code);
		return ApiResponse.success(null);
	}

	@PostMapping("/disconnect")
	public ApiResponse<Void> disconnect(
			@AuthenticationPrincipal Long memberId
	) {
		gitHubOAuthService.disconnect(memberId);
		return ApiResponse.success(null);
	}
}
