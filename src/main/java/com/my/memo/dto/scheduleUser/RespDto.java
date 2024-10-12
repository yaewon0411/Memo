package com.my.memo.dto.scheduleUser;

import com.my.memo.domain.scheduleUser.ScheduleUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class UserAssignRespDto {
        private Long scheduleId;
        private String name;

        public UserAssignRespDto(ScheduleUser scheduleUser) {
            this.scheduleId = scheduleUser.getSchedule().getId();
            this.name = scheduleUser.getUser().getName();
        }
    }
}
