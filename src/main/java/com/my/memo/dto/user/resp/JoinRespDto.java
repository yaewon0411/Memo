package com.my.memo.dto.user.resp;

import com.my.memo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class JoinRespDto {
    private Long userId;
    private String name;

    public JoinRespDto(User user) {
        this.userId = user.getId();
        this.name = user.getName();
    }
}