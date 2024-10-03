package com.my.memo.util.api;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * API 응답을 생성하기 위한 클래스입니다
 *
 * API 요청 성공 또는 실패에 따라 ApiResult 객체를 생성하고 반환합니다
 */
public class ApiUtil<T> {

    /**
     * API 요청 성공 응답
     *
     * @param data 성공 시 반환할 데이터
     * @param <T> 데이터의 타입
     * @return 응답을 포함한 ApiResult 객체
     */
    public static <T>ApiResult<T> success(T data){
        return new ApiResult<>(true, data, null);
    }


    /**
     * API 요청 실패: 오류 응답
     *
     * @param status HTTP 상태 코드
     * @param msg 오류 메시지
     * @param <T> 오류 데이터 타입
     * @return 오류 정보를 포함한 ApiResult 객체
     */
    public static <T>ApiResult<T> error(int status, String msg){
        return new ApiResult<>(false, null, new ApiError(msg, status));
    }

    /**
     * API 요청 실패: 추가적인 오류 데이터를 포함하는 응답
     *
     * @param status HTTP 상태 코드
     * @param msg 오류 메시지
     * @param errorMap 오류 데이터
     * @param <T> 오류 데이터의 타입
     * @return 오류 정보 및 추가 데이터를 포함한 ApiResult 객체
     */
    public static <T>ApiResult<T> error(int status, String msg, T errorMap){
        return new ApiResult<>(false, errorMap, new ApiError(msg, status));
    }

}
