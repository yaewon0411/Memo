package com.my.memo.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    public static class ScheduleModifyReqDto {

        @Size(min = 1, max = 512, message = "1자에서 512자 사이로 입력해야 합니다")
        private String content;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime startAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime endAt;

        private Boolean isPublic;
    }

    @NoArgsConstructor
    @Getter
    public static class ScheduleCreateReqDto {

        @NotBlank(message = "할 일을 입력해야 합니다")
        @Size(min = 1, max = 512, message = "1자에서 512자 사이로 입력해야 합니다")
        private String content;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime startAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime endAt;

        @NotNull(message = "공개 여부를 선택해야 합니다")
        private Boolean isPublic;
    }
}
