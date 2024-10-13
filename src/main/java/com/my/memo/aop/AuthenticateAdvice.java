package com.my.memo.aop;

import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticateAdvice {

    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(AuthenticateUser)")
    public Object validateAndGetUser(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("AuthenticatedAdvice 동작");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "인증 정보가 없습니다. 재로그인 해주세요");
        }

        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다"));

        Object[] args = proceedingJoinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof User) {
                args[i] = userPS;
                break;
            }
        }

        return proceedingJoinPoint.proceed(args);
    }
}
