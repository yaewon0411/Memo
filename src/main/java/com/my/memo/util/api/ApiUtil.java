package com.my.memo.util.api;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiUtil<T> {

    public static <T>ApiResult<T> success(T data){
        return new ApiResult<>(true, data, null);
    }

    public static <T>ApiResult<T> error(int status, String msg){
        return new ApiResult<>(false, null, new ApiError(msg, status));
    }
    public static <T>ApiResult<T> error(int status, String msg, T errorMap){
        return new ApiResult<>(false, null, new ApiError(msg, status, errorMap));
    }

}
