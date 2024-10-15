package com.my.memo.domain.comment;

import com.my.memo.domain.schedule.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.schedule = :schedule")
    Page<Comment> findCommentsWithUserBySchedule(Schedule schedule, PageRequest pageRequest);

    @Modifying
    @Query("delete from Comment c where c.user.id = :userId")
    int deleteByUserId(@Param(value = "userId") Long userId);

    @Modifying
    @Query("delete from Comment c where c.schedule.id = :scheduleId")
    int deleteByScheduleId(@Param(value = "scheduleId") Long scheduleId);


}

