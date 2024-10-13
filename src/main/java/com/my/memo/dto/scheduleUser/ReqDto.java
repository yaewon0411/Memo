package com.my.memo.dto.scheduleUser;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    public static class UserAssignReqDto {
        private Long scheduleId;
        private Long userId;
    }
}
