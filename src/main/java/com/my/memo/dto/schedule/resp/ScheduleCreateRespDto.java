package com.my.memo.dto.schedule.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.schedule.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ScheduleCreateRespDto {

    private Long id;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endAt;
    private Long userId;
    private Boolean isPublic;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    private String weatherOnCreation;

    public ScheduleCreateRespDto(Schedule schedule) {
        this.id = schedule.getId();
        this.content = schedule.getContent();
        this.startAt = schedule.getStartAt();
        this.endAt = schedule.getEndAt();
        this.userId = schedule.getUser().getId();
        this.isPublic = schedule.isPublic();
        this.createdAt = schedule.getCreatedAt();
        this.weatherOnCreation = schedule.getWeatherOnCreation();
    }
}