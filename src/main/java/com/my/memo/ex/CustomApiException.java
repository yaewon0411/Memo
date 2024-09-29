package com.my.memo.ex;

import lombok.Getter;
import org.springframework.http.HttpStatus;

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
