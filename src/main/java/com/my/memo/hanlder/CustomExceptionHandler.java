package com.my.memo.hanlder;

import com.my.memo.ex.CustomApiException;
import com.my.memo.util.api.ApiError;
import com.my.memo.util.api.ApiResult;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리를 위한 클래스입니다
 * <p>
 * 애플리케이션 전반에서 발생하는 예외를 처리하고, 적절한 HTTP 응답을 클라이언트에게 반환합니다
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<ApiError>> handleGeneralException(Exception e) {
        log.error("예기치 못한 오류 발생: {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 에러가 발생했습니다. 잠시 후 다시 시도해주세요"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<ApiError>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String errorMsg = String.format("잘못된 HTTP 메서드를 사용했습니다. 가능한 HTTP 메서드: %s", e.getSupportedHttpMethods());
        return new ResponseEntity<>(ApiResult.error(HttpStatus.METHOD_NOT_ALLOWED.value(), errorMsg), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> validationException(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errorMap.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(ApiResult.error(HttpStatus.BAD_REQUEST.value(), "유효성 검사 실패", errorMap), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResult<ApiError>> handleNoResourceFoundException(NoResourceFoundException e) {
        return new ResponseEntity<>(ApiResult.error(HttpStatus.NOT_FOUND.value(), "요청된 URI를 찾을 수 없습니다"), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<ApiResult<ApiError>> apiException(CustomApiException e) {
        return new ResponseEntity<>(ApiResult.error(e.getErrorCode().getStatus(), e.getMessage()), HttpStatusCode.valueOf(e.getErrorCode().getStatus()));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> queryParameterValidationException(ConstraintViolationException e) {
        Map<String, String> errorMap = new HashMap<>();
        e.getConstraintViolations().forEach(error ->
                errorMap.put(((PathImpl) (error.getPropertyPath())).getLeafNode().getName(), error.getMessage()));
        return new ResponseEntity<>(ApiResult.error(HttpStatus.BAD_REQUEST.value(), "유효성 검사 실패", errorMap), HttpStatus.BAD_REQUEST);
    }
}
