package com.nova.nova_server.global.apiPayload.code.success;

import org.springframework.http.HttpStatus;

public interface SuccessCode {
	HttpStatus getHttpStatus();
	String getCode();
	String getMessage();
}