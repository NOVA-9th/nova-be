package com.nova.nova_server.domain.auth.controller;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.error.AuthErrorCode;
import com.nova.nova_server.domain.auth.util.JwtUtil;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "API 테스트, 로그인 테스트", description = "테스트 목적으로 사용")
@RestController
@RequestMapping("/auth/test")
@RequiredArgsConstructor
public class AuthTestController {

	private final MemberRepository memberRepository;
	private final JwtUtil jwtUtil;

	@Operation(
		summary = "현재 로그인 사용자 조회",
		description = "인증된 사용자 컨텍스트에서 사용자 정보를 조회합니다."
	)
	@GetMapping("/me")
	public ApiResponse<Member> getCurrentUser(
		@Parameter(description = "인증된 사용자 ID", required = true)
		@AuthenticationPrincipal Long memberId
	) {
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

	@Operation(
		summary = "테스트 사용자 생성",
		description = "2번 ID 를 가진 사용자를 생성합니다."
	)
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

	@Operation(
		summary = "테스트 토큰 발급",
		description = "요청한 사용자 ID로 7일 유효 테스트용 JWT를 발급합니다."
	)
	@PostMapping("/token")
	public ApiResponse<AuthResponse> generateTestToken(
		@Parameter(description = "토큰 발급 대상 사용자 ID (null이면 비회원 토큰)")
		@RequestBody Long userId
	) {	
		// 유저 존재 여부 확인
		if (userId != null && !memberRepository.existsById(userId)) {
			throw new com.nova.nova_server.global.apiPayload.exception.NovaException(
				AuthErrorCode.OAUTH_AUTHORIZATION_FAILED
			);
		}

		// 7일짜리 JWT 토큰 발급
		long sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000;
		String token = jwtUtil.generateToken(userId, sevenDaysInMillis);
		
		return ApiResponse.success(AuthResponse.builder()
			.accessToken(token)
			.memberId(userId)
			.email("test@example.com")
			.name("테스트 유저")
			.build());
	}
}
