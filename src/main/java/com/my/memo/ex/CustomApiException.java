package com.my.memo.ex;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 *
 * API 요청 처리 중 발생한 예외를 나타내기 위한 커스텀 예외입니다
 * 예외 발생 시 HTTP 상태 코드와 예외 메시지를 함께 저장합니다
 */
@Getter
public class CustomApiException extends RuntimeException{
    private final int status;
    private final String msg;

    public CustomApiException(int status, String msg) {
        super(msg);
        this.status = status;
        this.msg = msg;
    }
}
