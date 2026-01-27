package com.nova.nova_server.domain.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
	String accessToken,
	Long memberId,
	String email,
	String name
) {
}
