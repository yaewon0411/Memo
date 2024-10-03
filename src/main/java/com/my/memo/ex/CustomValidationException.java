package com.my.memo.ex;

import lombok.Getter;

import java.util.Map;
/**
 *
 * 유효성 검사 중 발생한 오류를 처리하기 위한 커스텀 예외입니다
 * 예외 발생 시 유효성 검사 오류 메시지와 필드별 오류 정보를 함께 저장합니다
 */
@Getter
public class CustomValidationException extends RuntimeException{

    private final Map<String, String> errorMap;

    public CustomValidationException(String message,  Map<String, String> errorMap) {
        super(message);
        this.errorMap = errorMap;
    }
}
