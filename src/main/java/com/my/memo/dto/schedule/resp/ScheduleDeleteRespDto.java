package com.my.memo.dto.schedule.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ScheduleDeleteRespDto {
    private Long id;
    private Boolean deleted;

    public ScheduleDeleteRespDto(Long id, Boolean deleted) {
        this.id = id;
        this.deleted = deleted;
    }
}
