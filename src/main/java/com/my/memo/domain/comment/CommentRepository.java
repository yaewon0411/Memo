package com.my.memo.domain.comment;

import com.my.memo.domain.comment.dto.ScheduleCommentCountDto;
import com.my.memo.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c left join fetch c.user u where c.schedule = :schedule")
    List<Comment> findCommentsWithUserBySchedule(Schedule schedule);

    @Query("select new com.my.memo.domain.comment.dto.ScheduleCommentCountDto(c.schedule, count(c)) " +
            "from Comment c " +
            "where c.schedule in :scheduleList " +
            "group by c.schedule")
    List<ScheduleCommentCountDto> countCommentsBySchedules(@Param("scheduleList") List<Schedule> scheduleList);
}

