package com.nova.nova_server.domain.auth.error;

import com.nova.nova_server.global.apiPayload.code.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	OAUTH_AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4010", "OAuth 인증에 실패했습니다."),
	OAUTH_TOKEN_EXCHANGE_FAILED(HttpStatus.BAD_REQUEST, "AUTH4000", "OAuth 토큰 교환에 실패했습니다."),
	OAUTH_USER_INFO_FAILED(HttpStatus.BAD_REQUEST, "AUTH4001", "OAuth 사용자 정보 조회에 실패했습니다."),
	ALREADY_CONNECTED_ACCOUNT(HttpStatus.FORBIDDEN, "AUTH4030", "이미 다른 사용자에게 연결된 계정입니다."),
	INVALID_AUTHORIZATION_CODE(HttpStatus.BAD_REQUEST, "AUTH4002", "유효하지 않은 인증 코드입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
