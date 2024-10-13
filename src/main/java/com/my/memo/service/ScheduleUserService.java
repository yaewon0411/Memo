package com.my.memo.service;

import com.my.memo.aop.AuthenticateUser;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.scheduleUser.ScheduleUser;
import com.my.memo.domain.scheduleUser.ScheduleUserRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.my.memo.dto.scheduleUser.ReqDto.UserAssignReqDto;
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
    @AuthenticateUser
    public UserAssignRespDto assignUserToSchedule(UserAssignReqDto userAssignReqDto, User user) {

        Schedule schedulePS = scheduleRepository.findById(userAssignReqDto.getScheduleId()).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
        );

        validateScheduleUserLimit(schedulePS);

        User userPS = userRepository.findById(userAssignReqDto.getUserId()).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 유저는 존재하지 않습니다")
        );

        ScheduleUser scheduleUserPS = scheduleUserRepository.save(new ScheduleUser(userPS, schedulePS));

        log.info("유저 ID {}: 일정 ID {}에 유저 ID {}를 할당", user.getId(), schedulePS.getId(), userPS.getId());

        return new UserAssignRespDto(scheduleUserPS);
    }

    //최대 인원으로 배정되었는지 검사
    private void validateScheduleUserLimit(Schedule schedule) {
        if (scheduleUserRepository.countScheduleUserBySchedule(schedule) >= 5) {
            throw new CustomApiException(HttpStatus.BAD_REQUEST.value(), "해당 일정에는 더 이상 유저를 배정할 수 없습니다");
        }
    }


}
