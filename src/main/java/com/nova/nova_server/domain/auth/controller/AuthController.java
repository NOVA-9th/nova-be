package com.nova.nova_server.domain.auth.controller;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.error.AuthErrorCode;
import com.nova.nova_server.domain.auth.util.JwtUtil;
import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.service.MemberService;
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

@Tag(name = "로그인", description = "로그인 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtil jwtUtil;
	private final MemberService memberService;

	@Operation(
		summary = "현재 로그인 사용자 조회",
		description = "인증된 사용자 컨텍스트에서 사용자 정보를 조회합니다."
	)
	@GetMapping("/me")
	public ApiResponse<MemberResponseDto> getCurrentUser(
		@Parameter(description = "인증된 사용자 ID", required = true)
		@AuthenticationPrincipal Long memberId
	) {
		if (memberId == null) {
			throw new com.nova.nova_server.global.apiPayload.exception.NovaException(
				AuthErrorCode.OAUTH_AUTHORIZATION_FAILED
			);
		}

		return ApiResponse.success(memberService.getMemberInfo(memberId));
	}

	@Operation(
		summary = "토큰 무효화",
		description = "토큰을 무효화합니다."
	)
	@PostMapping("/invalidate")
	public ApiResponse<Void> invalidate(
		@AuthenticationPrincipal Long memberId
	) {
		// JWT 토큰 무효화를 구현하기 위해서 blacklist 를 사용하는 방법이 있음
		// 이걸 구현하기 위해 RDB 에 테이블을 만드는건 JWT의 장점을 모두 잃어버리는 것이기 때문에 구현하지 않음
		// 혹은 redis 를 사용하는 방법이 있으나, 현재 프로젝트에서 redis 도입을 결정하지 않았음
		// 따라서 이 API 는 의도적으로 구현하지 않은 채로 남겨둠
		return ApiResponse.success(null);
	}

	@Operation(
		summary = "테스트 사용자 생성",
		description = "2번 ID 를 가진 사용자를 생성합니다."
	)
	@PostMapping("/create-test-user")
	@Transactional
	public ApiResponse<MemberResponseDto> createMockUser() {
		MemberResponseDto member = memberService.createTestMember();
		return ApiResponse.success(member);
	}

	@Operation(
		summary = "관리자 계정 생성",
		description = "관리자 계정이 없을 때만 생성합니다. 소셜 계정은 연결되지 않습니다."
	)
	@PostMapping("/create-admin")
	@Transactional
	public ApiResponse<MemberResponseDto> createAdmin(
		@RequestBody MemberRequestDto requestDto
	) {
		MemberResponseDto member = memberService.createAdminMember(requestDto);
		return ApiResponse.success(member);
	}

	@Operation(
		summary = "테스트 토큰 발급",
		description = "요청한 사용자 ID로 7일 유효 테스트용 JWT를 발급합니다."
	)
	@PostMapping("/generate-test-token")
	public ApiResponse<AuthResponse> generateTestToken(
		@Parameter(description = "토큰 발급 대상 사용자 ID (null이면 비회원 토큰)")
		@RequestBody Long userId
	) {	
		// 유저 존재 여부 확인
		MemberResponseDto member = memberService.getMemberInfo(userId);

		// 7일짜리 JWT 토큰 발급
		long sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000;
		String token = jwtUtil.generateToken(member.getId(), sevenDaysInMillis);
		
		return ApiResponse.success(AuthResponse.builder()
			.accessToken(token)
			.memberId(userId)
			.email(member.getEmail())
			.name(member.getName())
			.build());
	}

	@Operation(
		summary = "관리자 토큰 발급",
		description = "등록된 관리자 계정으로 7일 유효 JWT를 발급합니다."
	)
	@PostMapping("/generate-admin-token")
	public ApiResponse<AuthResponse> generateAdminToken() {
		MemberResponseDto admin = memberService.getAdminMemberInfo();

		long sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000;
		String token = jwtUtil.generateToken(admin.getId(), sevenDaysInMillis);

		return ApiResponse.success(AuthResponse.builder()
			.accessToken(token)
			.memberId(admin.getId())
			.email(admin.getEmail())
			.name(admin.getName())
			.build());
	}
}
