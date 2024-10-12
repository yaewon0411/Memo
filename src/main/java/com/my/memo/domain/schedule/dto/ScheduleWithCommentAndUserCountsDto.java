package com.my.memo.domain.schedule.dto;

import com.my.memo.domain.schedule.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class ScheduleWithCommentAndUserCountsDto {

    private Schedule schedule;
    private Long commentCnt;
    private Long assignedUserCnt;

    public ScheduleWithCommentAndUserCountsDto(Schedule schedule, Long commentCnt, Long assignedUserCnt) {
        this.schedule = schedule;
        this.commentCnt = commentCnt;
        this.assignedUserCnt = assignedUserCnt;
    }
}
