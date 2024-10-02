package com.my.memo.domain.schedule;

import com.my.memo.domain.base.BaseEntity;
import com.my.memo.domain.user.User;
import com.my.memo.dto.schedule.ReqDto;
import com.my.memo.service.ScheduleService;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.my.memo.dto.schedule.ReqDto.*;
import static com.my.memo.service.ScheduleService.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@SuperBuilder
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

    public void modify(ScheduleModifyReqDto scheduleModifyReqDto){
        if(scheduleModifyReqDto.getContent() != null)
            this.content = scheduleModifyReqDto.getContent();
        if(scheduleModifyReqDto.getStartAt() != null)
            this.startAt = scheduleModifyReqDto.getLocalDateTimeStartAt();
        if(scheduleModifyReqDto.getEndAt() != null)
            this.endAt = scheduleModifyReqDto.getLocalDateTimeEndAt();
        if(scheduleModifyReqDto.getIsPublic() != null)
            this.isPublic = scheduleModifyReqDto.getIsPublic();
    }

}
