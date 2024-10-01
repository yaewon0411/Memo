package com.my.memo.hanlder;

import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.CustomValidationException;
import com.my.memo.util.api.ApiUtil;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleNoResourceFoundException(NoHandlerFoundException ex) {
        return new ResponseEntity<>(ApiUtil.error(HttpStatus.NOT_FOUND.value(), "요청된 URI를 찾을 수 없습니다"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException e){
        return new ResponseEntity<>(ApiUtil.error(e.getStatus(), e.getMsg()), HttpStatusCode.valueOf(e.getStatus()));
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> validationException(CustomValidationException e){
        return new ResponseEntity<>(ApiUtil.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getErrorMap()), HttpStatus.BAD_REQUEST);
    }
}
