package com.my.memo.dto.user.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.my.memo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRespDto {
    private Long userId;
    private String name;
    @JsonIgnore
    private String jwt;

    public LoginRespDto(User user, String jwt) {
        this.userId = user.getId();
        this.name = user.getName();
        this.jwt = jwt;
    }
}
