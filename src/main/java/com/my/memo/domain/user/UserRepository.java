package com.my.memo.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsUserByEmail(String email);

    Optional<User> findUserByEmail(String email);


    @Query("select distinct u from User u left join fetch u.scheduleList s where u.id = :userId ")
    Optional<User> findUserWithSchedulesById(@Param(value = "userId") Long userId);
}
