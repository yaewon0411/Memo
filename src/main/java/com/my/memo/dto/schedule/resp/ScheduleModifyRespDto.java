package com.my.memo.dto.schedule.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.schedule.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ScheduleModifyRespDto {
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endAt;
    private Boolean isPublic;

    public ScheduleModifyRespDto(Schedule schedule) {
        this.content = schedule.getContent();
        this.startAt = schedule.getStartAt();
        this.endAt = schedule.getEndAt();
        this.isPublic = schedule.isPublic();
    }
}
