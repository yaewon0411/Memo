package com.my.memo.domain.scheduleUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleUserRepository extends JpaRepository<ScheduleUser, Long> {


}


