package com.my.memo.domain.comment;

import com.my.memo.domain.base.BaseEntity;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.User;
import com.my.memo.dto.comment.req.CommentModifyReqDto;
import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Builder
    public Comment(String content, User user, Schedule schedule, Long id) {
        this.content = content;
        this.user = user;
        this.schedule = schedule;
        this.id = id;
        schedule.getCommentList().add(this);
    }

    public boolean isAuthor(User user) {
        return this.user.getId().equals(user.getId());
    }

    public void validateCommentAccess(User user) {
        if (!user.isAdmin() && !this.isAuthor(user)) {
            throw new CustomApiException(ErrorCode.FORBIDDEN_COMMENT_ACCESS);
        }
    }

    public void validateBelongsTo(Schedule schedule) {
        if (!this.schedule.getId().equals(schedule.getId())) {
            throw new CustomApiException(ErrorCode.COMMENT_NOT_IN_SCHEDULE);
        }
    }

    public void modify(CommentModifyReqDto commentModifyReqDto) {
        this.content = commentModifyReqDto.getContent();
    }
}
