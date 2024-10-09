package com.my.memo.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class UserLogoutRespDto {
        private Boolean logout;
        private Long userId;

        public UserLogoutRespDto(Boolean logout, Long id) {
            this.logout = logout;
            this.userId = id;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class UserDeleteRespDto {
        private Boolean deleted;
        private Long userId;

        public UserDeleteRespDto(Boolean deleted, Long userId) {
            this.deleted = deleted;
            this.userId = userId;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class UserModifyRespDto {
        private String name;
        private String email;

        public UserModifyRespDto(User user) {
            this.name = user.getName();
            this.email = user.getEmail();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class UserRespDto {
        private String name;
        private String email;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDateTime joinDay; //가입일

        public UserRespDto(User user) {
            this.name = user.getName();
            this.email = user.getEmail();
            this.joinDay = user.getCreatedAt();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class LoginRespDto {
        private Long userId;
        private String name;

        public LoginRespDto(User user) {
            this.userId = user.getId();
            this.name = user.getName();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class JoinRespDto {
        private Long userId;
        private String name;

        public JoinRespDto(User user) {
            this.userId = user.getId();
            this.name = user.getName();
        }
    }
}
