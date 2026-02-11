package com.nova.nova_server.domain.batch.common.error;

import com.nova.nova_server.global.apiPayload.code.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BatchErrorCode implements ErrorCode {

    BATCH_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH500", "배치 생성에 실패했습니다."),
    BATCH_TIMEOUT(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH501", "배치 처리 시간이 초과되었습니다."),
    BATCH_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH502", "배치 결과 조회에 실패했습니다."),
    OPENAI_API_ERROR(HttpStatus.BAD_GATEWAY, "BATCH503", "AI API 호출에 실패했습니다."),
    CARD_TYPE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH504", "카드 타입을 찾을 수 없습니다."),
    NO_ARTICLES_TO_PROCESS(HttpStatus.OK, "BATCH200", "처리할 새로운 아티클이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
