package com.nova.nova_server.domain.member.error;

import com.nova.nova_server.global.apiPayload.code.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4040", "존재하지 않는 사용자입니다."),
    MEMBER_FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER4030", "본인의 정보만 수정할 수 있습니다."),
    MEMBER_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER4000", "이름은 필수 입력 항목입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}