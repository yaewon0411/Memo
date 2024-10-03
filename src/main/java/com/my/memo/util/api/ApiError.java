package com.my.memo.util.api;
import lombok.Getter;

/**
 * API 오류 정보를 나타내는 클래스입니다
 *
 * API 요청이 실패했을 때 반환되는 오류 메시지와 상태 코드를 포함합니다
 *
 * @param <T>
 */
@Getter
public class ApiError<T> {

    private final String msg;
    private final int status;

    public ApiError(String msg, int status) {
        this.msg = msg;
        this.status = status;
    }
}
