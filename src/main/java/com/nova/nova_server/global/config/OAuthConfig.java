package com.nova.nova_server.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OAuthConfig {

	@Value("${oauth.google.client-id}")
	private String googleClientId;

	@Value("${oauth.google.client-secret}")
	private String googleClientSecret;

	@Value("${oauth.google.redirect-uri}")
	private String googleRedirectUri;

	@Value("${oauth.kakao.client-id}")
	private String kakaoClientId;

	@Value("${oauth.kakao.client-secret}")
	private String kakaoClientSecret;

	@Value("${oauth.kakao.redirect-uri}")
	private String kakaoRedirectUri;
}
