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
/**
 * 유효성 검사를 위한 AOP 클래스입니다
 *
 * `PostMapping`과 `PatchMapping` 어노테이션이 적용된 메서드에 대해 유효성 검사를 수행합니다
 * `BindingResult` 객체를 확인하여 오류가 있는 경우, 커스텀 예외인 CustomValidationException을 발생시킵니다
 * 유효성 검사에 통과한 경우에는 원래 메서드를 계속 진행시킵니다
 */
@Component
@Aspect
public class ValidationAdvice {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping(){};

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void patchMapping(){};

    /**
     * `@PostMapping 또는 `@PatchMapping`이 적용된 메서드 실행 전후로 유효성 검사를 수행하는 어드바이스입니다
     *
     * 메서드의 인자 중 `BindingResult`가 존재하고, 해당 객체에 오류가 있을 경우 CustomValidationException 예외를 발생시킵니다.
     *
     * @param proceedingJoinPoint 메서드 실행 시점의 정보를 담고 있는 객체
     * @return 원래 메서드의 반환값
     * @throws Throwable 원래 메서드 실행 중 발생하는 예외 또는 유효성 검사 실패 시 발생하는 CustomValidationException
     */
    @Around("postMapping() || patchMapping()")
    //@Around(" patchMapping()")
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult bindingResult) {
                if(bindingResult.hasErrors()){
                    Map<String, String> errorMap = new HashMap<>();
                    for (FieldError fieldError : bindingResult.getFieldErrors()) {
                        errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    }
                    throw new CustomValidationException("유효성 검사 실패", errorMap);
                }
            }
        }
        return proceedingJoinPoint.proceed();
    }

}
