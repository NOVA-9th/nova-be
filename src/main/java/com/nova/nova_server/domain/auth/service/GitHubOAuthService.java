package com.nova.nova_server.domain.auth.service;

import com.nova.nova_server.domain.auth.dto.AuthResponse;
import com.nova.nova_server.domain.auth.dto.GitHubTokenResponse;
import com.nova.nova_server.domain.auth.dto.GitHubUserInfo;
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
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubOAuthService {

	private final OAuthConfig oAuthConfig;
	private final JwtUtil jwtUtil;
	private final MemberRepository memberRepository;
	private final RestTemplate restTemplate = new RestTemplate();

	public String getAuthorizationUrl() {
		String clientId = oAuthConfig.getGithubClientId();
		String redirectUri = oAuthConfig.getGithubRedirectUri();
		String scope = "read:user user:email";

		String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
		String encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8);

		return String.format(
			"https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=%s",
			clientId, encodedRedirectUri, encodedScope
		);
	}

	@Transactional
	public AuthResponse handleCallback(String code) {
		try {
			GitHubUserInfo userInfo = authorize(code);

			// 사용자 정보로 Member 조회 또는 생성
			Member member = findOrCreateMember(userInfo);

			// JWT 토큰 발급
			String jwtToken = jwtUtil.generateToken(member.getId());

			return AuthResponse.builder()
					.accessToken(jwtToken)
					.memberId(member.getId())
					.email(member.getEmail())
					.name(member.getName())
					.build();
		} catch (NovaException e) {
			throw e;
		} catch (Exception e) {
			log.error("GitHub OAuth callback 처리 중 오류 발생", e);
			throw new NovaException(AuthErrorCode.OAUTH_AUTHORIZATION_FAILED);
		}
	}

	@Transactional
	public void connect(Long memberId, String code) {
		try {
			GitHubUserInfo userInfo = authorize(code);
			String githubId = String.valueOf(userInfo.getId());

			// GitHub 계정이 이미 다른 사용자에게 연결되었음
			if (memberRepository.existsByGithubId(githubId)) {
				throw new NovaException(AuthErrorCode.ALREADY_CONNECTED_ACCOUNT);
			}

			// 현재 로그인한 멤버 찾기
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

			// 로그인한 멤버에게 GitHub 계정 연결
			member.connectGithub(githubId);
		} catch (NovaException e) {
			throw e;
		} catch (Exception e) {
			log.error("GitHub OAuth callback 처리 중 오류 발생", e);
			throw new NovaException(AuthErrorCode.OAUTH_AUTHORIZATION_FAILED);
		}
	}

	@Transactional
	public void disconnect(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));
		member.disconnectGithub();
	}

	private GitHubUserInfo authorize(String code) {
		// Authorization code로 access token 교환
		GitHubTokenResponse tokenResponse = exchangeCodeForToken(code);
		if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
			throw new NovaException(AuthErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED);
		}

		// Access token으로 사용자 정보 조회
		GitHubUserInfo userInfo = getUserInfo(tokenResponse.getAccessToken());
		if (userInfo == null || userInfo.getId() == null) {
			throw new NovaException(AuthErrorCode.OAUTH_USER_INFO_FAILED);
		}

		return userInfo;
	}

	private GitHubTokenResponse exchangeCodeForToken(String code) {
		try {
			String tokenUrl = "https://github.com/login/oauth/access_token";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Accept", "application/json");

			Map<String, String> params = new HashMap<>();
			params.put("client_id", oAuthConfig.getGithubClientId());
			params.put("client_secret", oAuthConfig.getGithubClientSecret());
			params.put("code", code);

			HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

			ResponseEntity<GitHubTokenResponse> response = restTemplate.postForEntity(
				tokenUrl, request, GitHubTokenResponse.class
			);

			return response.getBody();
		} catch (Exception e) {
			log.error("GitHub OAuth token 교환 실패", e);
			throw new NovaException(AuthErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED);
		}
	}

	private GitHubUserInfo getUserInfo(String accessToken) {
		try {
			String userInfoUrl = "https://api.github.com/user";

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			headers.set("Accept", "application/vnd.github.v3+json");

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<GitHubUserInfo> response = restTemplate.exchange(
				userInfoUrl, HttpMethod.GET, entity, GitHubUserInfo.class
			);

			return response.getBody();
		} catch (Exception e) {
			log.error("GitHub 사용자 정보 조회 실패", e);
			throw new NovaException(AuthErrorCode.OAUTH_USER_INFO_FAILED);
		}
	}

	private Member findOrCreateMember(GitHubUserInfo userInfo) {
		String githubId = String.valueOf(userInfo.getId());

		String name = userInfo.getName();
		if (name == null || name.isEmpty()) {
			name = userInfo.getLogin();
		}

		String finalName = name;

		// GitHub ID로 조회, 없으면 새 계정 생성
		return memberRepository.findByGithubId(githubId)
			.orElseGet(() -> memberRepository.save(Member.builder()
					.email(userInfo.getEmail())
					.name(finalName)
					.githubId(githubId)
					.build()));
	}
}
