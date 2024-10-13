package com.my.memo.domain.scheduleUser;

import com.my.memo.domain.base.BaseEntity;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScheduleUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule; //최대 5명까지 배정 가능

    @Builder
    public ScheduleUser(User user, Schedule schedule) {
        this.user = user;
        this.schedule = schedule;
    }
}
