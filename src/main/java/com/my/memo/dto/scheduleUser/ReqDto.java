package com.my.memo.dto.scheduleUser;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    public static class UserAssignReqDto {
        @Positive
        private Long scheduleId;
        @Positive
        private Long userId;
    }
}
