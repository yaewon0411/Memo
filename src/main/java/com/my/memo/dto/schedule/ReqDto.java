package com.my.memo.dto.schedule;

import com.my.memo.ex.CustomApiException;
import com.my.memo.util.CustomUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    public static class ScheduleModifyReqDto{

        @Size(min = 1, max = 512)
        private String content;

        @Pattern(regexp = "^(19|20)\\d\\d-(0[1-9]|1[0-2])-([0-2][0-9]|3[01])\\s([01]\\d|2[0-3]):([0-5]\\d)$",
                message = "시작 시간은 yyyy-MM-dd HH:mm 형식이어야 합니다"
        )
        private String startAt;

        @Pattern(regexp = "^(19|20)\\d\\d-(0[1-9]|1[0-2])-([0-2][0-9]|3[01])\\s([01]\\d|2[0-3]):([0-5]\\d)$",
                message = "종료 시간은 yyyy-MM-dd HH:mm 형식이어야 합니다"
        )
        private String endAt;

        private Boolean isPublic;

        public LocalDateTime getLocalDateTimeEndAt(){
            return CustomUtil.scheduleTimeToLocalDateTime(this.endAt);
        }
        public LocalDateTime getLocalDateTimeStartAt(){
            return CustomUtil.scheduleTimeToLocalDateTime(this.startAt);
        }
    }
    @NoArgsConstructor
    @Getter
    public static class ScheduleCreateReqDto{

        @NotBlank(message = "할 일을 입력해야 합니다")
        @Size(min = 1, max = 512, message = "1자에서 512자 사이로 입력해야 합니다")
        private String content;

        @Pattern(regexp = "^(19|20)\\d\\d-(0[1-9]|1[0-2])-([0-2][0-9]|3[01])\\s([01]\\d|2[0-3]):([0-5]\\d)$",
                message = "시작 시간은 yyyy-MM-dd HH:mm 형식이어야 합니다"
        )
        private String startAt;

        @Pattern(regexp = "^(19|20)\\d\\d-(0[1-9]|1[0-2])-([0-2][0-9]|3[01])\\s([01]\\d|2[0-3]):([0-5]\\d)$",
                message = "종료 시간은 yyyy-MM-dd HH:mm 형식이어야 합니다"
        )
        private String endAt;

        @NotNull(message = "공개 여부를 선택해야 합니다")
        private Boolean isPublic;
    }
}
