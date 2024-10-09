package com.my.memo.domain.dummy;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.Role;
import com.my.memo.domain.user.User;
import com.my.memo.util.CustomPasswordUtil;

import java.time.LocalDateTime;

public class DummyObject {

    public User newUser(String email, Long id) {
        return User.builder()
                .id(id)
                .name("user1")
                .email(email)
                .password(CustomPasswordUtil.encode("user1234"))
                .role(Role.USER)
                .build();
    }

    public User mockUser() {
        return User.builder()
                .name("user1")
                .email("user1234@naver.com")
                .password(CustomPasswordUtil.encode("user1234"))
                .role(Role.USER)
                .build();
    }

    public Schedule newSchedule(User user, boolean isPublic, Long id) {
        return Schedule.builder()
                .id(id)
                .user(user)
                .content("할 일")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now())
                .isPublic(isPublic)
                .build();
    }

}
