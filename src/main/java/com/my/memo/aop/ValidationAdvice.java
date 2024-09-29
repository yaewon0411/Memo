package com.my.memo.aop;
import com.my.memo.ex.CustomValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class ValidationAdvice {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping(){};

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void patchMapping(){};

    @Around("postMapping() || patchMapping()")
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        for (Object arg : args) {
            BindingResult bindingResult = (BindingResult) arg;
            if(bindingResult.hasErrors()){
                Map<String, String> errorMap = new HashMap<>();
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
                throw new CustomValidationException("유효성 검사 실패", errorMap);
            }
        }
        return proceedingJoinPoint.proceed();
    }

}
