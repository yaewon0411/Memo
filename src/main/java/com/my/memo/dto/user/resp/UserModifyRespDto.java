package com.my.memo.dto.user.resp;

import com.my.memo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserModifyRespDto {
    private String name;
    private String email;

    public UserModifyRespDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
