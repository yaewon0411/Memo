package com.my.memo.domain.user;

import lombok.Getter;

@Getter
public enum Role {

    USER(Authority.USER), //일반 유저
    ADMIN(Authority.ADMIN); //관리자

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public static class Authority {
        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";
    }
}
