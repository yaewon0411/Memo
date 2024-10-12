package com.my.memo.service;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.scheduleUser.ScheduleUser;
import com.my.memo.domain.scheduleUser.ScheduleUserRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.my.memo.dto.scheduleUser.RespDto.UserAssignRespDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleUserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ScheduleUserRepository scheduleUserRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserAssignRespDto assignUserToSchedule(Long scheduleId, Long userId, HttpServletRequest request) {

        // 인증된 유저 정보 가져오기
        Long authUserId = (Long) request.getAttribute("userId");
        userRepository.findById(authUserId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
        );
        log.info("인증된 유저: 유저 ID {}", authUserId);

        Schedule schedulePS = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
        );

        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 유저는 존재하지 않습니다")
        );

        ScheduleUser scheduleUserPS = scheduleUserRepository.save(new ScheduleUser(userPS, schedulePS));

        log.info("유저 ID {}: 일정 ID {}에 유저 ID {}를 할당", authUserId, scheduleId, userId);

        return new UserAssignRespDto(scheduleUserPS);
    }


}
