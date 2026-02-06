package com.nova.nova_server.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GitHubUserInfo {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("login")
	private String login;

	@JsonProperty("name")
	private String name;

	@JsonProperty("email")
	private String email;

	@JsonProperty("avatar_url")
	private String avatarUrl;

	@JsonProperty("bio")
	private String bio;
}
