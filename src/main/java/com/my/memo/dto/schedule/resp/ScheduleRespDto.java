package com.my.memo.dto.schedule.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.scheduleUser.ScheduleUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class ScheduleRespDto {
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