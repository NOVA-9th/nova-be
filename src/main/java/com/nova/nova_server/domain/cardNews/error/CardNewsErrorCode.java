package com.nova.nova_server.domain.cardNews.error;

import com.nova.nova_server.global.apiPayload.code.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CardNewsErrorCode implements ErrorCode {

    CARDNEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "CARDNEWS4040", "존재하지 않는 카드뉴스입니다."),
    CARDNEWS_ALREADY_HIDDEN(HttpStatus.CONFLICT, "CARDNEWS4090", "이미 숨긴 카드뉴스입니다.");;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}