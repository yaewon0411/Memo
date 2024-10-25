package com.my.memo.ex;

import lombok.Getter;

/**
 * API 요청 처리 중 발생한 예외를 나타내기 위한 커스텀 예외입니다
 * 예외 발생 시 HTTP 상태 코드와 예외 메시지를 함께 저장합니다
 */
@Getter
public class CustomApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomApiException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}
