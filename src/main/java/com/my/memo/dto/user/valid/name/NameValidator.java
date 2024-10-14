package com.my.memo.dto.user.valid.name;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<IsKoreanOrEnglish, String> {


    @Override
    public void initialize(IsKoreanOrEnglish constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {

        //null은 NotBlank로 검사하고
        if (name == null)
            return true;

        String[] words = name.split(" ");
        for (String word : words) {
            if (!word.chars().allMatch(c -> isKoreanCharacter(c) || isEnglishCharacter(c))) {
                return false;
            }
        }

        return true;
    }

    private boolean isKoreanCharacter(int c) {
        return c >= '가' && c <= '힣';
    }

    private boolean isEnglishCharacter(int c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

}
