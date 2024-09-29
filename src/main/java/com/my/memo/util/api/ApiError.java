package com.my.memo.util.api;
import lombok.Getter;


@Getter
public class ApiError<T> {

    private final String msg;
    private final int status;
    private T errorMap;

    public ApiError(String msg, int status) {
        this.msg = msg;
        this.status = status;
    }

    public ApiError(String msg, int status, T errorMap) {
        this.msg = msg;
        this.status = status;
        this.errorMap = errorMap;
    }
}
