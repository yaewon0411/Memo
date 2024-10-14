package com.my.memo.aop;

import com.my.memo.aop.valid.ValidateEntity;
import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.ex.CustomApiException;
import com.my.memo.util.entity.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@RequiredArgsConstructor
public class EntityValidatorAspect {

    private final EntityValidator entityValidator;

    @Around("@annotation(ValidateEntity)")
    public Object validateEntity(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            ValidateEntity annotation = parameters[i].getAnnotation(ValidateEntity.class);
            if (annotation != null && args[i] instanceof Long) {
                args[i] = validateAndConvert(annotation.entityType(), (Long) args[i]);
            }
        }
        return joinPoint.proceed(args);
    }


    private Object validateAndConvert(Class<?> entityType, Long id) {
        if (Schedule.class.equals(entityType)) {
            return entityValidator.validateAndGetSchedule(id);
        } else if (Comment.class.equals(entityType)) {
            return entityValidator.validateAndGetComment(id);
        }
        throw new CustomApiException(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 어쩌고 날릴것");
    }
}
