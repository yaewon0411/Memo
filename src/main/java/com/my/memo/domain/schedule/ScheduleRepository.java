package com.my.memo.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, Dao {


    @Query("select count(s) from Schedule s where s.isPublic = true")
    int countPublicSchedules();

    @Query("select count(s) from Schedule s where s.user.id = :userId")
    int countUserSchedules(@Param(value = "userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Schedule s where s.user.id = :userId")
    int deleteByUserId(@Param(value = "userId") Long userId);

}
