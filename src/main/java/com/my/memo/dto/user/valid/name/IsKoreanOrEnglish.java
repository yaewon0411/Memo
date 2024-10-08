package com.my.memo.dto.user.valid.name;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameValidator.class)
public @interface IsKoreanOrEnglish {
    String message() default "이름은 영문 또는 한글만 입력 가능합니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
