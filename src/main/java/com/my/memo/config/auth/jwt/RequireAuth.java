package com.my.memo.config.auth.jwt;

import com.my.memo.domain.user.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
    Role role() default Role.USER;
}

/*
 * ADMIN -> 일정 수정/삭제 포함 모두 가능
 * USER -> 일정 수정/삭제는 불가능
 *
 *
 * */
