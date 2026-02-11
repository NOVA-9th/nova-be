package com.nova.nova_server.domain.member.error;

import com.nova.nova_server.global.apiPayload.code.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4040", "존재하지 않는 사용자입니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4041", "관리자 계정이 존재하지 않습니다."),
    ADMIN_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER4090", "이미 관리자 계정이 존재합니다."),
    MEMBER_READ_FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER4030", "본인의 정보만 조회할 수 있습니다."),
    MEMBER_FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER4030", "본인의 정보만 수정할 수 있습니다."),
    MEMBER_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER4031", "일반 사용자는 본인 계정만 탈퇴할 수 있습니다."),
    ADMIN_DELETE_ADMIN_FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER4032", "관리자 계정은 탈퇴시킬 수 없습니다."),
    MEMBER_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER4000", "이름은 필수 입력 항목입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}