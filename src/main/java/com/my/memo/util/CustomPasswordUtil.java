package com.my.memo.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 비밀번호 인코딩과 디코딩을 위한 유틸리티 클래스입니다
 * 회원가입과 로그인 시 사용됩니다
 */
public class CustomPasswordUtil {


    // 비밀번호 해싱
    public static String encode(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    //비밀번호 검증
    public static boolean matches(String plainTextPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
