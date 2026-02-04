package com.nova.nova_server.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoUserInfo {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount;

	@JsonProperty("properties")
	private KakaoProperties properties;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class KakaoAccount {
		@JsonProperty("email")
		private String email;

		@JsonProperty("profile")
		private KakaoProfile profile;

		@JsonProperty("email_needs_agreement")
		private Boolean emailNeedsAgreement;

		@JsonProperty("is_email_valid")
		private Boolean isEmailValid;

		@JsonProperty("is_email_verified")
		private Boolean isEmailVerified;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class KakaoProfile {
		@JsonProperty("nickname")
		private String nickname;

		@JsonProperty("profile_image_url")
		private String profileImageUrl;

		@JsonProperty("thumbnail_image_url")
		private String thumbnailImageUrl;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class KakaoProperties {
		@JsonProperty("nickname")
		private String nickname;

		@JsonProperty("profile_image")
		private String profileImage;

		@JsonProperty("thumbnail_image")
		private String thumbnailImage;
	}

	// 편의 메서드: 이메일 가져오기
	public String getEmail() {
		return kakaoAccount != null ? kakaoAccount.getEmail() : null;
	}

	// 편의 메서드: 닉네임 가져오기
	public String getNickname() {
		if (kakaoAccount != null && kakaoAccount.getProfile() != null) {
			return kakaoAccount.getProfile().getNickname();
		}
		if (properties != null) {
			return properties.getNickname();
		}
		return null;
	}
}
