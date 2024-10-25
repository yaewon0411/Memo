package com.my.memo.dto.scheduleUser.resp;

import com.my.memo.domain.schedule.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AssignedUserDeleteRespDto {
    private boolean deleted;
    private Long scheduleId;
    private int deletedAssignedUserCnt;

    public AssignedUserDeleteRespDto(boolean deleted, Schedule schedule, int deletedAssignedUserCnt) {
        this.deleted = deleted;
        this.scheduleId = schedule.getId();
        this.deletedAssignedUserCnt = deletedAssignedUserCnt;
    }
}