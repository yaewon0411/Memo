package com.my.memo.domain.scheduleUser;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleUserRepository extends JpaRepository<ScheduleUser, Long> {

    Long countScheduleUserBySchedule(Schedule schedule);

    @Query("select su from ScheduleUser su left join fetch su.user u where su.schedule = :schedule")
    List<ScheduleUser> findScheduleUserBySchedule(@Param(value = "schedule") Schedule schedule);

    int deleteByUser(User user);

}


