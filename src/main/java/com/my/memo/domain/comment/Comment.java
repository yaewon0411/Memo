package com.my.memo.domain.comment;

import com.my.memo.domain.base.BaseEntity;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.my.memo.dto.comment.ReqDto.CommentModifyReqDto;


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

    public void modify(CommentModifyReqDto commentModifyReqDto) {
        this.content = commentModifyReqDto.getContent();
    }
}
