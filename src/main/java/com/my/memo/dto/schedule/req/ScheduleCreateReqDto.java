package com.my.memo.dto.schedule.req;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.User;
import com.my.memo.util.CustomUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ScheduleCreateReqDto {

    @NotBlank(message = "할 일을 입력해야 합니다")
    @Size(min = 1, max = 512, message = "1자에서 512자 사이로 입력해야 합니다")
    private String content;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$", message = "유효하지 않은 날짜 형식입니다 (yyyy-MM-dd HH:mm)")
    private String startAt;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$", message = "유효하지 않은 날짜 형식입니다 (yyyy-MM-dd HH:mm)")
    private String endAt;

    @NotNull(message = "공개 여부를 선택해야 합니다")
    private Boolean isPublic;

    public Schedule toEntity(User user, String weatherOnCreation) {
        return Schedule.builder()
                .startAt(CustomUtil.stringToLocalDateTime(this.startAt))
                .endAt(CustomUtil.stringToLocalDateTime(this.endAt))
                .content(this.content)
                .user(user)
                .isPublic(this.isPublic)
                .weatherOnCreation(weatherOnCreation)
                .build();
    }
}