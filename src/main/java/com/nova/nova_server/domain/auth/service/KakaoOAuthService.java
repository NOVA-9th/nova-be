package com.nova.nova_server.domain.auth.service;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.dto.KakaoTokenResponse;
import com.nova.nova_server.domain.auth.dto.KakaoUserInfo;
import com.nova.nova_server.domain.auth.error.AuthErrorCode;
import com.nova.nova_server.domain.auth.util.JwtUtil;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.error.MemberErrorCode;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.global.apiPayload.exception.NovaException;
import com.nova.nova_server.global.config.OAuthConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthService {

	private final OAuthConfig oAuthConfig;
	private final JwtUtil jwtUtil;
	private final MemberRepository memberRepository;
	private final RestTemplate restTemplate = new RestTemplate();

	public String getAuthorizationUrl(String state) {
		String clientId = oAuthConfig.getKakaoClientId();
		String redirectUri = oAuthConfig.getKakaoRedirectUri();
		String responseType = "code";

		String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

		String baseUrl = String.format(
			"https://kauth.kakao.com/oauth/authorize?response_type=%s&client_id=%s&redirect_uri=%s",
			responseType, clientId, encodedRedirectUri
		);
		if (state != null && !state.isBlank()) {
			String encodedState = URLEncoder.encode(state, StandardCharsets.UTF_8);
			return baseUrl + "&state=" + encodedState;
		}
		return baseUrl;
	}

	@Transactional
	public AuthResponse handleCallback(String code) {
		try {
			KakaoUserInfo userInfo = authorize(code);

			// 사용자 정보로 Member 조회 또는 생성
			Member member = findOrCreateMember(userInfo);

			// JWT 토큰 발급
			String jwtToken = jwtUtil.generateToken(member.getId(), member.getRole());

			return AuthResponse.builder()
					.accessToken(jwtToken)
					.memberId(member.getId())
					.email(member.getEmail())
					.name(member.getName())
					.build();
		} catch (NovaException e) {
			throw e;
		} catch (Exception e) {
			log.error("Kakao OAuth callback 처리 중 오류 발생", e);
			throw new NovaException(AuthErrorCode.OAUTH_AUTHORIZATION_FAILED);
		}
	}

	@Transactional
	public void connect(Long memberId, String code) {
		try {
			KakaoUserInfo userInfo = authorize(code);
			String kakaoId = String.valueOf(userInfo.getId());

			// 카카오 계정이 이미 다른 사용자에게 연결되었음
			if (memberRepository.existsByKakaoId(kakaoId)) {
				throw new NovaException(AuthErrorCode.ALREADY_CONNECTED_ACCOUNT);
			}

			// 현재 로그인한 멤버 찾기
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

			// 로그인한 멤버에게 카카오 계정 연결
			member.connectKakao(kakaoId);
		} catch (NovaException e) {
			throw e;
		} catch (Exception e) {
			log.error("Kakao OAuth callback 처리 중 오류 발생", e);
			throw new NovaException(AuthErrorCode.OAUTH_AUTHORIZATION_FAILED);
		}
	}

	@Transactional
	public void disconnect(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));
		member.disconnectKakao();
	}

	private KakaoUserInfo authorize(String code) {
		// Authorization code로 access token 교환
		KakaoTokenResponse tokenResponse = exchangeCodeForToken(code);
		if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
			throw new NovaException(AuthErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED);
		}

		// Access token으로 사용자 정보 조회
		KakaoUserInfo userInfo = getUserInfo(tokenResponse.getAccessToken());
		if (userInfo == null || userInfo.getId() == null) {
			throw new NovaException(AuthErrorCode.OAUTH_USER_INFO_FAILED);
		}

		return userInfo;
	}

	private KakaoTokenResponse exchangeCodeForToken(String code) {
		try {
			String tokenUrl = "https://kauth.kakao.com/oauth/token";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("grant_type", "authorization_code");
			params.add("client_id", oAuthConfig.getKakaoClientId());
			params.add("client_secret", oAuthConfig.getKakaoClientSecret());
			params.add("redirect_uri", oAuthConfig.getKakaoRedirectUri());
			params.add("code", code);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

			ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
				tokenUrl, request, KakaoTokenResponse.class
			);

			return response.getBody();
		} catch (Exception e) {
			log.error("Kakao OAuth token 교환 실패", e);
			throw new NovaException(AuthErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED);
		}
	}

	private KakaoUserInfo getUserInfo(String accessToken) {
		try {
			String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
				userInfoUrl, HttpMethod.GET, entity, KakaoUserInfo.class
			);

			return response.getBody();
		} catch (Exception e) {
			log.error("Kakao 사용자 정보 조회 실패", e);
			throw new NovaException(AuthErrorCode.OAUTH_USER_INFO_FAILED);
		}
	}

	private Member findOrCreateMember(KakaoUserInfo userInfo) {
		String kakaoId = String.valueOf(userInfo.getId());

		String nickname = userInfo.getNickname();
		if (nickname == null || nickname.isEmpty()) {
			throw new NovaException(AuthErrorCode.OAUTH_USER_INFO_FAILED);
		}

		// 카카오 ID로 조회, 없으면 새 계정 생성
		return memberRepository.findByKakaoId(kakaoId)
			.orElseGet(() -> memberRepository.save(Member.builder()
					.email(userInfo.getEmail())
					.name(nickname)
					.role(Member.MemberRole.USER)
					.kakaoId(kakaoId)
					.build()));
	}
}
