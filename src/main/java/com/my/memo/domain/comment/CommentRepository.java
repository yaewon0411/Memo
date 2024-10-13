package com.my.memo.domain.comment;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c left join fetch c.user u where c.schedule = :schedule")
    List<Comment> findCommentsWithUserBySchedule(Schedule schedule);

    @Modifying
    int deleteByUser(User user);


}

