package com.my.memo.domain.scheduleUser;

import com.my.memo.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ScheduleUserRepository extends JpaRepository<ScheduleUser, Long> {

    int countScheduleUserBySchedule(Schedule schedule);

    @Query("select su from ScheduleUser su left join fetch su.user u where su.schedule = :schedule")
    List<ScheduleUser> findScheduleUserBySchedule(@Param(value = "schedule") Schedule schedule);

    @Modifying(clearAutomatically = true)
    @Query("delete from ScheduleUser su where su.user.id = :userId or su.schedule in :scheduleList")
    int deleteByUserId(@Param(value = "userId") Long userId, @Param(value = "scheduleList") List<Schedule> scheduleList);

    @Modifying(clearAutomatically = true)
    @Query("delete from ScheduleUser su where su.schedule = :schedule and su.user.id in :userIdList")
    int deleteByUserIdsAndSchedule(@Param(value = "userIdList") List<Long> userIdList, @Param(value = "schedule") Schedule schedule);


    @Query("select su.user.id from ScheduleUser su where su.schedule = :schedule")
    Set<Long> getAssignedUserIdsBySchedule(Schedule schedule);

    @Modifying
    @Query("delete from ScheduleUser su where su.schedule = :schedule")
    int deleteBySchedule(Schedule schedule);
}


