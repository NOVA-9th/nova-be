package com.nova.nova_server.global.apiPayload.exception;

import com.nova.nova_server.global.apiPayload.code.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NovaException extends RuntimeException {
	private final ErrorCode errorCode;
}