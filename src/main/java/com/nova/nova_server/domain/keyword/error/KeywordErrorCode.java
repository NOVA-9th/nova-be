package com.nova.nova_server.domain.keyword.error;

import com.nova.nova_server.global.apiPayload.code.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum KeywordErrorCode implements ErrorCode {

    INTEREST_NOT_FOUND(HttpStatus.NOT_FOUND, "KEYWORD4040", "요청한 관심분야를 찾을 수 없습니다."),
    INTEREST_NOT_FOUND_BAD_REQUEST(HttpStatus.BAD_REQUEST, "KEYWORD4000", "요청한 관심분야를 찾을 수 없습니다."),

    KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "KEYWORD4041", "요청한 키워드를 찾을 수 없습니다."),
    KEYWORD_NOT_FOUND_BAD_REQUEST(HttpStatus.BAD_REQUEST, "KEYWORD4001", "요청한 키워드를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
