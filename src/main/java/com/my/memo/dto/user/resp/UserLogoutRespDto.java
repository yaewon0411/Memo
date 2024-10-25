package com.my.memo.dto.user.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserLogoutRespDto {
    private Boolean logout;
    private Long userId;

    public UserLogoutRespDto(Boolean logout, Long id) {
        this.logout = logout;
        this.userId = id;
    }
}
