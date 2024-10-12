package com.my.memo.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserScheduleFilter {
        @Min(0)
        private Long page = 0L;

        @Min(1)
        private Long limit = 10L;

        @Pattern(regexp = "^(30m|1h|1d|1w|1m|3m|6m)$", message = "유효하지 않은 modifiedAt 값입니다")
        private String modifiedAt;

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
        private String startModifiedAt;

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
        private String endModifiedAt;

    }

    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    public static class PublicScheduleFilter {
        @Min(0)
        private Long page = 0L;

        @Min(1)
        private Long limit = 10L;

        @Pattern(regexp = "^(30m|1h|1d|1w|1m|3m|6m)$", message = "유효하지 않은 modifiedAt 값입니다")
        private String modifiedAt;

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
        private String startModifiedAt;

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
        private String endModifiedAt;

        @Length(max = 12, message = "유효하지 않은 authorName 값입니다")
        private String authorName;

    }

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

        public Schedule toEntity(User user) {
            return Schedule.builder()
                    .startAt(this.startAt)
                    .endAt(this.endAt)
                    .content(this.content)
                    .user(user)
                    .isPublic(this.isPublic)
                    .build();
        }
    }
}
