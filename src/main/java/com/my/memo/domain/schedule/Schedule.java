package com.my.memo.domain.schedule;

import com.my.memo.domain.base.BaseEntity;
import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.scheduleUser.ScheduleUser;
import com.my.memo.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.my.memo.dto.schedule.ReqDto.ScheduleModifyReqDto;
import static jakarta.persistence.CascadeType.REMOVE;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "schedules")
public class Schedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    private String content;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "schedule", cascade = REMOVE, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();


    @OneToMany(mappedBy = "schedule", cascade = REMOVE, orphanRemoval = true)
    private List<ScheduleUser> assignedUserList = new ArrayList<>();

    @Builder
    public Schedule(String content, LocalDateTime startAt, LocalDateTime endAt, boolean isPublic, User user) {
        this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isPublic = isPublic;
        this.user = user;
        user.getScheduleList().add(this);
    }

    public void assignUser(ScheduleUser scheduleUser) {
        this.assignedUserList.add(scheduleUser);
    }


    public void modify(ScheduleModifyReqDto scheduleModifyReqDto) {
        if (scheduleModifyReqDto.getContent() != null)
            this.content = scheduleModifyReqDto.getContent();
        if (scheduleModifyReqDto.getStartAt() != null)
            this.startAt = scheduleModifyReqDto.getStartAt();
        if (scheduleModifyReqDto.getEndAt() != null)
            this.endAt = scheduleModifyReqDto.getEndAt();
        if (scheduleModifyReqDto.getIsPublic() != null)
            this.isPublic = scheduleModifyReqDto.getIsPublic();
    }

}
