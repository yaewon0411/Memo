package com.my.memo.dto.user;

import com.my.memo.domain.user.User;
import com.my.memo.util.CustomUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class UserLogoutRespDto{
        public UserLogoutRespDto(Boolean logout, Long id) {
            this.logout = logout;
            this.userId = id;
        }
        private Boolean logout;
        private Long userId;
    }

    @NoArgsConstructor
    @Getter
    public static class UserDeleteRespDto{
        public UserDeleteRespDto(Boolean deleted, Long userId) {
            this.deleted = deleted;
            this.userId = userId;
        }

        private Boolean deleted;
        private Long userId;
    }

    @NoArgsConstructor
    @Getter
    public static class UserModifyRespDto {
        public UserModifyRespDto(User user) {
            this.name = user.getName();
            this.email = user.getEmail();
        }

        private String name;
        private String email;
    }

    @NoArgsConstructor
    @Getter
    public static class UserRespDto{
        public UserRespDto(User user) {
            this.name = user.getName();
            this.email = user.getEmail();
            this.joinDay = CustomUtil.localDateTimeToScheduleTime(user.getCreatedAt());
        }

        private String name;
        private String email;
        private String joinDay; //가입일
    }

    @Getter
    @NoArgsConstructor
    public static class LoginRespDto{
        public LoginRespDto(User user) {
            this.userId = user.getId();
            this.name = user.getName();
        }

        private Long userId;
        private String name;
    }

    @NoArgsConstructor
    @Getter
    public static class JoinRespDto{
        public JoinRespDto(Long id, User user){
            this.userId = id;
            this.name = user.getName();
        }
        private Long userId;
        private String name;
    }
}
