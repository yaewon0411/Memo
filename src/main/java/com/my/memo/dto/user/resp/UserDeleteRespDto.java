package com.my.memo.dto.user.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserDeleteRespDto {
    private Boolean deleted;
    private Long userId;

    public UserDeleteRespDto(Boolean deleted, Long userId) {
        this.deleted = deleted;
        this.userId = userId;
    }
}