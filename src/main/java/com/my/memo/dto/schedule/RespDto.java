package com.my.memo.dto.schedule;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.util.CustomUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RespDto {
    /*
    * 스케줄 id
    * 작성자명
    * 생성일
    * 수정일
    * content
    *
            * 까지만 내보내기
    * */
    @NoArgsConstructor
    @Getter
    public static class ScheduleListRespDto{
        public ScheduleListRespDto(List<Schedule> scheduleList, boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
            this.scheduleRespDtoList = scheduleList.stream()
                    .map(ScheduleRespDto::new)
                    .collect(Collectors.toList());
        }
        private List<ScheduleRespDto> scheduleRespDtoList;
        private boolean hasNextPage;


        @NoArgsConstructor
        @Getter
        public static class ScheduleRespDto{
            public ScheduleRespDto(Schedule schedule) {
                this.content = schedule.getContent();
                this.isPublic = schedule.isPublic();
                this.id = schedule.getId();
                this.name = schedule.getUser().getName();
                this.createdAt = CustomUtil.localDateTimeToScheduleTime(schedule.getCreatedAt());
                this.modifiedAt = CustomUtil.localDateTimeToScheduleTime(schedule.getLastModifiedAt());
            }

            private Long id;
            private String name;
            private String content;
            private Boolean isPublic;
            private String createdAt;
            private String modifiedAt;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class ScheduleDeleteRespDto{
        public ScheduleDeleteRespDto(Long id, Boolean deleted) {
            this.id = id;
            this.deleted = deleted;
        }

        private Long id;
        private Boolean deleted;
    }

    @NoArgsConstructor
    @Getter
    public static class ScheduleModifyRespDto{
        private String content;
        private String startAt;
        private String endAt;
        private Boolean isPublic;

        public ScheduleModifyRespDto(Schedule schedule) {
            this.content = schedule.getContent();
            this.startAt = CustomUtil.localDateTimeToScheduleTime(schedule.getStartAt());
            this.endAt = CustomUtil.localDateTimeToScheduleTime(schedule.getEndAt());
            this.isPublic = schedule.isPublic();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class UserScheduleListRespDto {

        public UserScheduleListRespDto(List<Schedule> scheduleList, boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
            this.scheduleRespDtoList = scheduleList.stream()
                    .map(ScheduleRespDto::new)
                    .collect(Collectors.toList());
        }
        private List<ScheduleRespDto> scheduleRespDtoList;
        private boolean hasNextPage;

        @NoArgsConstructor
        @Getter
        public static class ScheduleRespDto{
            public ScheduleRespDto(Schedule schedule) {
                this.content = schedule.getContent();
                this.isPublic = schedule.isPublic();
                this.id = schedule.getId();
                this.createdAt = CustomUtil.localDateTimeToScheduleTime(schedule.getCreatedAt());
                this.modifiedAt = CustomUtil.localDateTimeToScheduleTime(schedule.getLastModifiedAt());
            }

            private Long id;
            private String content;
            private Boolean isPublic;
            private String createdAt;
            private String modifiedAt;
        }

    }


    @NoArgsConstructor
    @Getter
    public static class ScheduleRespDto{
        public ScheduleRespDto(Schedule schedule) {
            this.content = schedule.getContent();
            this.startAt = CustomUtil.localDateTimeToScheduleTime(schedule.getStartAt());
            this.endAt = CustomUtil.localDateTimeToScheduleTime(schedule.getEndAt());
            this.isPublic = schedule.isPublic();
            this.id = schedule.getId();
            this.createdAt = CustomUtil.localDateTimeToScheduleTime(schedule.getCreatedAt());
            this.modifiedAt = CustomUtil.localDateTimeToScheduleTime(schedule.getLastModifiedAt());
            this.name = schedule.getUser().getName();
        }

        private Long id;
        private String content;
        private String startAt;
        private String endAt;
        private Boolean isPublic;
        private String createdAt;
        private String modifiedAt;
        private String name;
    }
    @NoArgsConstructor
    @Getter
    public static class ScheduleCreateRespDto{

        public ScheduleCreateRespDto(Schedule schedule, Long scheduleId) {
            this.id = scheduleId;
            this.content = schedule.getContent();
            this.startAt = schedule.getStartAt();
            this.endAt = schedule.getEndAt();
            this.userId = schedule.getUser().getId();
            this.isPublic = schedule.isPublic();
            this.createdAt = CustomUtil.localDateTimeToScheduleTime(schedule.getCreatedAt());
        }

        private Long id;
        private String content;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private Long userId;
        private Boolean isPublic;
        private String createdAt;
    }
}
