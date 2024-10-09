package com.my.memo.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.schedule.Schedule;
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
    public static class ScheduleListRespDto {
        private List<ScheduleRespDto> scheduleRespDtoList;
        private boolean hasNextPage;
        private int size;

        public ScheduleListRespDto(List<Schedule> scheduleList, boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
            this.scheduleRespDtoList = scheduleList.stream()
                    .map(ScheduleRespDto::new)
                    .collect(Collectors.toList());
            this.size = this.scheduleRespDtoList.size();
        }

        @NoArgsConstructor
        @Getter
        public static class ScheduleRespDto {
            private Long id;
            private String name;
            private String content;
            private Boolean isPublic;
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
            private LocalDateTime createdAt;
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
            private LocalDateTime modifiedAt;
            private int commentCnt;

            public ScheduleRespDto(Schedule schedule) {
                this.content = schedule.getContent();
                this.isPublic = schedule.isPublic();
                this.id = schedule.getId();
                this.name = schedule.getUser().getName();
                this.createdAt = schedule.getCreatedAt();
                this.modifiedAt = schedule.getLastModifiedAt();
                this.commentCnt = schedule.getCommentList().size();
            }
        }
    }


    @NoArgsConstructor
    @Getter
    public static class ScheduleDeleteRespDto {
        private Long id;
        private Boolean deleted;

        public ScheduleDeleteRespDto(Long id, Boolean deleted) {
            this.id = id;
            this.deleted = deleted;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ScheduleModifyRespDto {
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

    @NoArgsConstructor
    @Getter
    public static class UserScheduleListRespDto {

        private List<ScheduleRespDto> scheduleRespDtoList;
        private boolean hasNextPage;
        private int size;

        public UserScheduleListRespDto(List<Schedule> scheduleList, boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
            this.scheduleRespDtoList = scheduleList.stream()
                    .map(ScheduleRespDto::new)
                    .collect(Collectors.toList());
            this.size = this.scheduleRespDtoList.size();
        }

        @NoArgsConstructor
        @Getter
        public static class ScheduleRespDto {
            private Long id;
            private String content;
            private Boolean isPublic;
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
            private LocalDateTime createdAt;
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
            private LocalDateTime modifiedAt;
            private int commentCnt;

            public ScheduleRespDto(Schedule schedule) {
                this.content = schedule.getContent();
                this.isPublic = schedule.isPublic();
                this.id = schedule.getId();
                this.createdAt = schedule.getCreatedAt();
                this.modifiedAt = schedule.getLastModifiedAt();
                this.commentCnt = schedule.getCommentList().size();
            }
        }

    }


    @NoArgsConstructor
    @Getter
    public static class ScheduleRespDto {
        private Long id;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime startAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime endAt;
        private Boolean isPublic;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime modifiedAt;
        private String name;

        public ScheduleRespDto(Schedule schedule) {
            this.content = schedule.getContent();
            this.startAt = schedule.getStartAt();
            this.endAt = schedule.getEndAt();
            this.isPublic = schedule.isPublic();
            this.id = schedule.getId();
            this.createdAt = schedule.getCreatedAt();
            this.modifiedAt = schedule.getLastModifiedAt();
            this.name = schedule.getUser().getName();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ScheduleCreateRespDto {

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

        public ScheduleCreateRespDto(Schedule schedule) {
            this.id = schedule.getId();
            this.content = schedule.getContent();
            this.startAt = schedule.getStartAt();
            this.endAt = schedule.getEndAt();
            this.userId = schedule.getUser().getId();
            this.isPublic = schedule.isPublic();
            this.createdAt = schedule.getCreatedAt();
        }
    }
}
