package com.my.memo.domain.schedule;

import com.my.memo.domain.base.BaseEntity;
import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.user.User;
import com.my.memo.dto.schedule.req.ScheduleModifyReqDto;
import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "schedules")
public class Schedule extends BaseEntity {

    /*
     * 해당 부분은 JPA 스펙상 원칙적으로 CascadeType.PERSIST이 없어도 orphanRemoval만으로 삭제되어야 하는 것이 맞습니다.
     * 하이버네이트 구현체에서는 해당 기능에 버그가 있고, 그래서 CascadeType.PERSIST(또는 ALL)이 함께 적용되어야 orphanRemoval이 동작합니다.
     * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 512)
    private String content;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private boolean isPublic;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private String weatherOnCreation;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    public Schedule(Long id, String content, LocalDateTime startAt, LocalDateTime endAt, boolean isPublic, User user, String weatherOnCreation) {
        this.id = id;
        this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isPublic = isPublic;
        this.user = user;
        this.weatherOnCreation = weatherOnCreation;
        user.getScheduleList().add(this);
    }

    public boolean isOwner(User user) {
        return this.user.getId().equals(user.getId());
    }

    public void validateScheduleAccess(User user) {
        if (!user.isAdmin() && !this.isOwner(user)) {
            throw new CustomApiException(ErrorCode.FORBIDDEN_SCHEDULE_ACCESS);
        }
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
