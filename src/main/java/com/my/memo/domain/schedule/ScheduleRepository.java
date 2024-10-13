package com.my.memo.domain.schedule;

import com.my.memo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, Dao {


    @Query("select distinct s from Schedule s " +
            "left join fetch s.user u " +
            "left join fetch u.scheduleList sl " +
            "where s.id = :scheduleId")
    Optional<Schedule> findScheduleWithUserById(@Param(value = "scheduleId") Long scheduleId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Schedule s where s.user = :user")
    int deleteByUser(@Param(value = "user") User user);

}
