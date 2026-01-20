package com.nova.nova_server.domain.auth.controller;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.error.AuthErrorCode;
import com.nova.nova_server.domain.auth.util.JwtUtil;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 로그인 테스트를 위한 컨트롤러
@RestController
@RequestMapping("/auth/test")
@RequiredArgsConstructor
public class AuthTestController {

	private final MemberRepository memberRepository;
	private final JwtUtil jwtUtil;

	@GetMapping("/me")
	public ApiResponse<Member> getCurrentUser(@AuthenticationPrincipal Long memberId) {
		if (memberId == null) {
			throw new com.nova.nova_server.global.apiPayload.exception.NovaException(
				AuthErrorCode.OAUTH_AUTHORIZATION_FAILED
			);
		}

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new com.nova.nova_server.global.apiPayload.exception.NovaException(
				AuthErrorCode.OAUTH_AUTHORIZATION_FAILED
			));

		return ApiResponse.success(member);
	}

	@PostMapping("/create-test-user")
	@Transactional
	public ApiResponse<Member> createMockUser() {
		Member mockUser = Member.builder()
		    .id(2L)
			.name("테스트 유저")
			.email("test@example.com")
			.level(Member.MemberLevel.NOVICE)
			.background("테스트 배경")
			.googleId("test-google-id")
			.build();

		Member savedUser = memberRepository.save(mockUser);
		return ApiResponse.success(savedUser);
	}

	@PostMapping("/token")
	public ApiResponse<AuthResponse> generateTestToken(@RequestBody Long userId) {	
		// 유저 존재 여부 확인
		if (userId != null && !memberRepository.existsById(userId)) {
			throw new com.nova.nova_server.global.apiPayload.exception.NovaException(
				AuthErrorCode.OAUTH_AUTHORIZATION_FAILED
			);
		}

		// 7일짜리 JWT 토큰 발급
		long sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000; // 7일을 밀리초로 변환
		String token = jwtUtil.generateTokenWithExpiration(userId, sevenDaysInMillis);
		
		return ApiResponse.success(AuthResponse.builder()
			.accessToken(token)
			.memberId(userId)
			.email("test@example.com")
			.name("테스트 유저")
			.build());
	}
}
