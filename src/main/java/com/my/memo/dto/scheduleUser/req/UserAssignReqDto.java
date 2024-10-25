package com.my.memo.dto.scheduleUser.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class UserAssignReqDto {
    @Size(min = 1)
    @Valid
    List<UserDto> userIdList;

    @NoArgsConstructor
    @Getter
    public static class UserDto {
        @Positive(message = "userId는 양수여야 합니다")
        @NotNull(message = "userId는 null일 수 없습니다")
        private Long userId;

    }

}