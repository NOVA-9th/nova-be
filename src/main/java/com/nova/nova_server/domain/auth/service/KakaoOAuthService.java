package com.nova.nova_server.domain.auth.service;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.dto.KakaoTokenResponse;
import com.nova.nova_server.domain.auth.dto.KakaoUserInfo;
import com.nova.nova_server.domain.auth.error.AuthErrorCode;
import com.nova.nova_server.domain.auth.util.JwtUtil;
import com.nova.nova_server.domain.member.entity.Member;
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

	public String getAuthorizationUrl() {
		String clientId = oAuthConfig.getKakaoClientId();
		String redirectUri = oAuthConfig.getKakaoRedirectUri();
		String responseType = "code";

		String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

		return String.format(
			"https://kauth.kakao.com/oauth/authorize?response_type=%s&client_id=%s&redirect_uri=%s",
			responseType, clientId, encodedRedirectUri
		);
	}

	@Transactional
	public AuthResponse handleCallback(String code) {
		try {
			// 1. Authorization code로 access token 교환
			KakaoTokenResponse tokenResponse = exchangeCodeForToken(code);
			if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
				throw new NovaException(AuthErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED);
			}

			// 2. Access token으로 사용자 정보 조회
			KakaoUserInfo userInfo = getUserInfo(tokenResponse.getAccessToken());
			if (userInfo == null || userInfo.getId() == null) {
				throw new NovaException(AuthErrorCode.OAUTH_USER_INFO_FAILED);
			}

			// 3. 사용자 정보로 Member 조회 또는 생성
			Member member = findOrCreateMember(userInfo);

			// 4. JWT 토큰 발급
			String jwtToken = jwtUtil.generateToken(member.getId());

			return new AuthResponse(jwtToken, member.getId(), member.getEmail(), member.getName());
		} catch (NovaException e) {
			throw e;
		} catch (Exception e) {
			log.error("Kakao OAuth callback 처리 중 오류 발생", e);
			throw new NovaException(AuthErrorCode.OAUTH_AUTHORIZATION_FAILED);
		}
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
		String email = userInfo.getEmail();
		String nickname = userInfo.getNickname();

		// 이메일이나 닉네임이 null이면 예외 발생
		if (email == null || email.isEmpty()) {
			throw new NovaException(AuthErrorCode.OAUTH_USER_INFO_FAILED);
		}
		if (nickname == null || nickname.isEmpty()) {
			throw new NovaException(AuthErrorCode.OAUTH_USER_INFO_FAILED);
		}

		// 카카오 ID로 조회, 없으면 새 계정 생성
		return memberRepository.findByKakaoId(kakaoId)
			.orElseGet(() -> memberRepository.save(Member.builder()
					.email(email)
					.name(nickname)
					.kakaoId(kakaoId)
					.build()));
	}
}
