package com.my.memo.aop;

import com.my.memo.aop.valid.RequireAuthenticatedUser;
import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthenticationAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("@annotaion(requireAuthenticatedUser)")
    public Object authenticatedUser(ProceedingJoinPoint proceedingJoinPoint, RequireAuthenticatedUser requireAuthenticatedUser) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Long userId = null;

        for (int i = 0; i < parameterNames.length; i++) {
            if ("userId".equals(parameterNames[i]) && args[i] instanceof Long) {
                userId = (Long) args[i];
                break;
            }
        }
        if (userId == null) {
            log.error("인증 실패: userId parameter is null");
            throw new CustomApiException(ErrorCode.INVALID_AUTH_INFO);
        }
        return proceedingJoinPoint.proceed();
    }
}
