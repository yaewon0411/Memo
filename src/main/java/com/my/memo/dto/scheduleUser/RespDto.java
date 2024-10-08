package com.my.memo.dto.scheduleUser;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class UserAssignRespDto {
        boolean isAssigned;

        public UserAssignRespDto(boolean isAssigned) {
            this.isAssigned = isAssigned;
        }
    }
}
