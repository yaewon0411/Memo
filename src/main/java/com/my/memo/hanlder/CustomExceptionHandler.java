package com.my.memo.hanlder;

import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.CustomValidationException;
import com.my.memo.util.api.ApiUtil;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
/**
 * 전역 예외 처리를 위한 클래스입니다
 *
 * 애플리케이션 전반에서 발생하는 예외를 처리하고, 적절한 HTTP 응답을 클라이언트에게 반환합니다
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);


    /**
     * NoResourceFoundException 발생 시 404 Not Found 응답을 반환
     *
     *
     * @param ex 발생한 NoResourceFoundException 예외 객체
     * @return 404 Not Found 응답을 포함한 ResponseEntity
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException ex) {
        return new ResponseEntity<>(ApiUtil.error(HttpStatus.NOT_FOUND.value(), "요청된 URI를 찾을 수 없습니다"), HttpStatus.NOT_FOUND);
    }


    /**
     * CustomApiException 발생 시 해당 예외의 HTTP 상태 코드와 메시지를 반환
     *
     * @param e 발생한 CustomApiException 예외 객체
     * @return 예외 상태 코드와 메시지를 포함한 ResponseEntity
     */
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException e){
        return new ResponseEntity<>(ApiUtil.error(e.getStatus(), e.getMsg()), HttpStatusCode.valueOf(e.getStatus()));
    }

    /**
     * CustomValidationException 발생 시 400 Bad Request 응답과 유효성 검사 오류 정보를 반환
     *
     * @param e 발생한 CustomValidationException 예외 객체
     * @return 유효성 검사 오류 정보를 포함한 ResponseEntity
     */
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> validationException(CustomValidationException e){
        return new ResponseEntity<>(ApiUtil.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getErrorMap()), HttpStatus.BAD_REQUEST);
    }

    /**
     * 쿼리 파라미터의 유효성 검사 실패 시 발생하는 ConstraintViolationException을 처리하고 400 Bad Request 응답을 반환
     *
     * @param e 발생한 ConstraintViolationException 예외 객체
     * @return 유효성 검사 실패 정보를 포함한 ResponseEntity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> queryParameterValidationException(ConstraintViolationException e){
        Map<String, String> errorMap = new HashMap<>();
        e.getConstraintViolations().forEach(error ->
                errorMap.put(((PathImpl)(error.getPropertyPath())).getLeafNode().getName(), error.getMessage()));
        return new ResponseEntity<>(ApiUtil.error(HttpStatus.BAD_REQUEST.value(), "유효성 검사 실패", errorMap), HttpStatus.BAD_REQUEST);
    }
}
