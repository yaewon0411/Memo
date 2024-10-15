package com.my.memo.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.dto.ScheduleWithCommentAndUserCountsDto;
import com.my.memo.domain.scheduleUser.ScheduleUser;
import com.my.memo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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
    public static class PublicScheduleListRespDto {
        private List<ScheduleRespDto> scheduleRespDtoList;
        private boolean hasNextPage;
        private int totalCounts;
        private int totalPages;
        private int currentPage;

        public PublicScheduleListRespDto(List<ScheduleWithCommentAndUserCountsDto> scheduleList, boolean hasNextPage, int totalPublicSchedules, int totalPages, int currentPage) {
            this.hasNextPage = hasNextPage;
            this.scheduleRespDtoList = scheduleList.stream()
                    .map(ScheduleRespDto::new)
                    .collect(Collectors.toList());
            this.totalCounts = totalPublicSchedules;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
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
            private int assignedUserCnt;

            public ScheduleRespDto(ScheduleWithCommentAndUserCountsDto scheduleDto) {
                this.content = scheduleDto.getSchedule().getContent();
                this.isPublic = scheduleDto.getSchedule().isPublic();
                this.id = scheduleDto.getSchedule().getId();
                this.name = scheduleDto.getSchedule().getUser().getName();
                this.createdAt = scheduleDto.getSchedule().getCreatedAt();
                this.modifiedAt = scheduleDto.getSchedule().getLastModifiedAt();
                this.assignedUserCnt = (int) scheduleDto.getAssignedUserCnt().longValue();
                this.commentCnt = (int) scheduleDto.getCommentCnt().longValue();
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
        private String name;
        private int totalCounts;
        private int totalPages;
        private int currentPage;

        public UserScheduleListRespDto(List<ScheduleWithCommentAndUserCountsDto> scheduleList, boolean hasNextPage, User user, int totalCounts, int totalPages, int currentPage) {
            this.hasNextPage = hasNextPage;
            this.scheduleRespDtoList = scheduleList.stream()
                    .map(s -> new ScheduleRespDto(s))
                    .collect(Collectors.toList());
            this.name = user.getName();
            this.totalCounts = totalCounts;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
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
            private int assignedUserCnt;


            public ScheduleRespDto(ScheduleWithCommentAndUserCountsDto scheduleDto) {
                this.content = scheduleDto.getSchedule().getContent();
                this.isPublic = scheduleDto.getSchedule().isPublic();
                this.id = scheduleDto.getSchedule().getId();
                this.createdAt = scheduleDto.getSchedule().getCreatedAt();
                this.modifiedAt = scheduleDto.getSchedule().getLastModifiedAt();
                this.assignedUserCnt = (int) scheduleDto.getAssignedUserCnt().longValue();
                this.commentCnt = (int) scheduleDto.getCommentCnt().longValue();
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

        private CommentsRespDto comments;
        private String weatherOnCreation;


        private List<AssignedUserDto> assignedUserList;

        public ScheduleRespDto(Schedule schedule, Page<Comment> commentPage, List<ScheduleUser> assignedUserList) {
            this.content = schedule.getContent();
            this.startAt = schedule.getStartAt();
            this.endAt = schedule.getEndAt();
            this.isPublic = schedule.isPublic();
            this.id = schedule.getId();
            this.createdAt = schedule.getCreatedAt();
            this.modifiedAt = schedule.getLastModifiedAt();
            this.name = schedule.getUser().getName();
            this.assignedUserList = assignedUserList.stream().map(AssignedUserDto::new).toList();
            this.comments = new CommentsRespDto(commentPage);
            this.weatherOnCreation = schedule.getWeatherOnCreation();
        }

        @NoArgsConstructor
        @Getter
        public static class CommentsRespDto {
            private List<CommentDto> commentList;
            private int totalPages;
            private long totalCounts;
            private boolean hasNextPage;
            private int currentPage;

            public CommentsRespDto(Page<Comment> commentPage) {
                this.commentList = commentPage.getContent().stream().map(CommentDto::new).toList();
                this.totalPages = commentPage.getTotalPages();
                this.totalCounts = commentPage.getTotalElements();
                this.hasNextPage = !commentPage.isLast();
                this.currentPage = commentPage.getNumber();
            }

            @NoArgsConstructor
            @Getter
            public static class CommentDto {

                private Long id;
                private String content;
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
                private LocalDateTime createdAt;
                private String name;
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
                private LocalDateTime modifiedAt;

                public CommentDto(Comment comment) {
                    this.id = comment.getId();
                    this.content = comment.getContent();
                    this.createdAt = comment.getCreatedAt();
                    this.name = comment.getUser().getName();
                    this.modifiedAt = comment.getLastModifiedAt();
                }

            }
        }

        @NoArgsConstructor
        @Getter
        public static class AssignedUserDto {
            private Long id;
            private String name;

            public AssignedUserDto(ScheduleUser scheduleUser) {
                this.id = scheduleUser.getUser().getId();
                this.name = scheduleUser.getUser().getName();
            }
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
}
