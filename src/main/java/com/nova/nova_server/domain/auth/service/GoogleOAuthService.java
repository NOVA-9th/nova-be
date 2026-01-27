package com.nova.nova_server.domain.auth.service;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.dto.GoogleTokenResponse;
import com.nova.nova_server.domain.auth.dto.GoogleUserInfo;
import com.nova.nova_server.domain.auth.util.JwtUtil;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.domain.auth.error.AuthErrorCode;
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
public class GoogleOAuthService {

	private final OAuthConfig oAuthConfig;
	private final JwtUtil jwtUtil;
	private final MemberRepository memberRepository;
	private final RestTemplate restTemplate = new RestTemplate();

	public String getAuthorizationUrl() {
		String clientId = oAuthConfig.getGoogleClientId();
		String redirectUri = oAuthConfig.getGoogleRedirectUri();
		String scope = "openid email profile";
		String responseType = "code";

		String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
		String encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8);

		return String.format(
			"https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s",
			clientId, encodedRedirectUri, responseType, encodedScope
		);
	}

	@Transactional
	public AuthResponse handleCallback(String code) {
		try {
			// 1. Authorization code로 access token 교환
			GoogleTokenResponse tokenResponse = exchangeCodeForToken(code);
			if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
				throw new NovaException(AuthErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED);
			}

			// 2. Access token으로 사용자 정보 조회
			GoogleUserInfo userInfo = getUserInfo(tokenResponse.getAccessToken());
			if (userInfo == null || userInfo.getEmail() == null) {
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
			log.error("Google OAuth callback 처리 중 오류 발생", e);
			throw new NovaException(AuthErrorCode.OAUTH_AUTHORIZATION_FAILED);
		}
	}

	private GoogleTokenResponse exchangeCodeForToken(String code) {
		try {
			String tokenUrl = "https://oauth2.googleapis.com/token";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("code", code);
			params.add("client_id", oAuthConfig.getGoogleClientId());
			params.add("client_secret", oAuthConfig.getGoogleClientSecret());
			params.add("redirect_uri", oAuthConfig.getGoogleRedirectUri());
			params.add("grant_type", "authorization_code");

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

			ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
				tokenUrl, request, GoogleTokenResponse.class
			);

			return response.getBody();
		} catch (Exception e) {
			log.error("Google OAuth token 교환 실패", e);
			throw new NovaException(AuthErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED);
		}
	}

	private GoogleUserInfo getUserInfo(String accessToken) {
		try {
			String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
				userInfoUrl, HttpMethod.GET, entity, GoogleUserInfo.class
			);

			return response.getBody();
		} catch (Exception e) {
			log.error("Google 사용자 정보 조회 실패", e);
			throw new NovaException(AuthErrorCode.OAUTH_USER_INFO_FAILED);
		}
	}

	private Member findOrCreateMember(GoogleUserInfo userInfo) {
		return memberRepository.findByGoogleId(userInfo.getId())
			.orElseGet(() -> memberRepository.save(Member.builder()
							.email(userInfo.getEmail())
							.name(userInfo.getName())
							.googleId(userInfo.getId())
							.build()));
	}
}
