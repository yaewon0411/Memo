package com.my.memo.dto.scheduleUser;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    @ToString
    public static class UserAssignReqDto {
        List<UserDto> userList;

        @NoArgsConstructor
        @Getter
        public static class UserDto {
            @Positive
            private Long userId;
        }

    }
}
