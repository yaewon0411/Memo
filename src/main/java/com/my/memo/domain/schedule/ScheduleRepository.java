package com.my.memo.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, Dao {

    @Query("select distinct s from Schedule s left join fetch s.commentList c where s in :scheduleList")
    List<Schedule> getCommentsBySchedules(@Param(value = "scheduleList") List<Schedule> scheduleList);
}
