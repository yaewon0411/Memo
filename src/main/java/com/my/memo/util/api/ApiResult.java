package com.my.memo.util.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * API 응답을 나타내는 클래스입니다
 *
 * API 요청의 성공 여부와 응답 데이터를 포함합니다
 * 성공 여부에 따라 data 또는 apiError를 포함하여 응답합니다
 *
 * @param <T> 응답 데이터 타입
 */
@AllArgsConstructor
@Builder
@Getter
public class ApiResult<T> {
    private boolean success;

    private T data;

    private final ApiError apiError;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime timestamp = LocalDateTime.now();



}
/*
*
* success
* time
* data
* */
