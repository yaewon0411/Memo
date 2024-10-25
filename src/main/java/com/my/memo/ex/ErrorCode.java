package com.my.memo.ex;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    AUTHOR_NOT_FOUND(404, "작성자 정보를 찾을 수 없습니다"),
    FORBIDDEN_COMMENT_ACCESS(403, "해당 댓글에 접근할 권한이 없습니다"),
    FORBIDDEN_SCHEDULE_ACCESS(403, "해당 일정에 접근할 권한이 없습니다"),
    EMAIL_ALREADY_EXISTS(409, "이미 사용 중인 이메일입니다"),
    INVALID_EMAIL(401, "이메일이 일치하지 않습니다"),
    INVALID_PASSWORD(401, "비밀번호가 일치하지 않습니다"),
    UNAUTHORIZED_ACCESS(401, "인증되지 않은 접근입니다"),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 댓글은 존재하지 않습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다");

    private final int status;
    private final String msg;

    ErrorCode(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
