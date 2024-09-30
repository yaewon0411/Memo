package com.my.memo.dto.user;

import com.my.memo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @Getter
    @NoArgsConstructor
    public static class LoginRespDto{
        public LoginRespDto(User user) {
            this.id = user.getId();
        }

        private Long id;
    }

    @NoArgsConstructor
    @Getter
    public static class JoinRespDto{
        public JoinRespDto(Long id, User user){
            this.id = id;
            this.name = user.getName();
        }
        private Long id;
        private String name;
    }
}
