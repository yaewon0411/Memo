package com.my.memo.dto.schedule.req;

import com.my.memo.util.CustomUtil;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ScheduleModifyReqDto {

    @Size(min = 1, max = 512, message = "1자에서 512자 사이로 입력해야 합니다")
    private String content;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$", message = "유효하지 않은 날짜 형식입니다 (yyyy-MM-dd HH:mm)")
    private String startAt;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$", message = "유효하지 않은 날짜 형식입니다 (yyyy-MM-dd HH:mm)")
    private String endAt;

    private Boolean isPublic;

    public LocalDateTime getStartAt() {
        return CustomUtil.stringToLocalDateTime(this.startAt);
    }

    public LocalDateTime getEndAt() {
        return CustomUtil.stringToLocalDateTime(this.endAt);
    }
}
