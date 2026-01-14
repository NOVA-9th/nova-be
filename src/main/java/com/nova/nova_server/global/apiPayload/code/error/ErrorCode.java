package com.nova.nova_server.global.apiPayload.code.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	HttpStatus getHttpStatus();
	String getCode();
	String getMessage();
}