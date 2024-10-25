package com.my.memo.aop;

import com.my.memo.aop.valid.RequireAuthenticatedUser;
import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthenticationAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around(value = "@annotaion(requireAuthenticatedUser)", argNames = "proceedingJoinPoint,requireAuthenticatedUser")
    public Object authenticatedUser(ProceedingJoinPoint proceedingJoinPoint, RequireAuthenticatedUser requireAuthenticatedUser) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new CustomApiException(ErrorCode.CONTEXT_NOT_EXIST);
        }
        HttpServletRequest request = requestAttributes.getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            log.error("인증 실패: userId is null");
            throw new CustomApiException(ErrorCode.INVALID_AUTH_INFO);
        }
        return proceedingJoinPoint.proceed();
    }
}
