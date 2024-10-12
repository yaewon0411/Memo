package com.my.memo.domain.comment.dto;

import com.my.memo.domain.schedule.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScheduleCommentCountDto {
    private Schedule schedule;
    private Long commentCnt;
}
