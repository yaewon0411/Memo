package com.my.memo.util;

import org.mindrot.jbcrypt.BCrypt;

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
