package com.my.memo.service;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.scheduleUser.ScheduleUser;
import com.my.memo.domain.scheduleUser.ScheduleUserRepository;
import com.my.memo.domain.user.Role;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import com.my.memo.util.entity.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.my.memo.dto.scheduleUser.ReqDto.AssignedUserDeleteReqDto;
import static com.my.memo.dto.scheduleUser.ReqDto.UserAssignReqDto;
import static com.my.memo.dto.scheduleUser.RespDto.AssignedUserDeleteRespDto;
import static com.my.memo.dto.scheduleUser.RespDto.UserAssignRespDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleUserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ScheduleUserRepository scheduleUserRepository;
    private final UserRepository userRepository;
    private final EntityValidator entityValidator;


    @Transactional
    public AssignedUserDeleteRespDto deleteAssignedUser(Long scheduleId, AssignedUserDeleteReqDto assignedUserDeleteReqDto, Long userId) {

        User userPS = entityValidator.validateAndGetUser(userId);

        Schedule schedulePS = entityValidator.validateAndGetSchedule(scheduleId);

        if (!userPS.getRole().equals(Role.ADMIN) && !schedulePS.getUser().equals(userPS)) {
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "해당 일정에 접근할 권한이 없습니다");
        }

        //일정에서 삭제할 유저 아이디 리스트
        List<Long> userIdListToDelete = assignedUserDeleteReqDto.getUserIdList().stream().map(AssignedUserDeleteReqDto.UserDto::getUserId).toList();

        //실제로 해당 일정에 배정되어 있는 유저인지 검증
        if (!scheduleUserRepository.getAssignedUserIdsBySchedule(schedulePS).containsAll(userIdListToDelete))
            throw new CustomApiException(HttpStatus.BAD_REQUEST.value(), "일정에 존재하지 않는 유저가 포함되어 있습니다");

        int deletedCnt = scheduleUserRepository.deleteByUserIdsAndSchedule(
                userIdListToDelete,
                schedulePS);

        return new AssignedUserDeleteRespDto(true, schedulePS, deletedCnt);
    }

    @Transactional
    public UserAssignRespDto assignUserToSchedule(Long scheduleId, UserAssignReqDto userAssignReqDto, Long userId) {

        User userPS = entityValidator.validateAndGetUser(userId);

        Schedule schedulePS = entityValidator.validateAndGetSchedule(scheduleId);

        if (!userPS.getRole().equals(Role.ADMIN) && !schedulePS.getUser().equals(userPS)) {
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "해당 일정에 접근할 권한이 없습니다");
        }

        //최대 인원 검사
        validateScheduleUserLimit(schedulePS, userAssignReqDto.getUserIdList().size());

        List<Long> userIdListToAssign = userAssignReqDto.getUserIdList().stream()
                .map(UserAssignReqDto.UserDto::getUserId)
                .toList();

        //이미 일정에 배정된 유저인지 검사
        Set<Long> assignedUserIdSet = scheduleUserRepository.getAssignedUserIdsBySchedule(schedulePS);
        for (Long userIdToAssign : userIdListToAssign) {
            if (assignedUserIdSet.contains(userIdToAssign))
                throw new CustomApiException(HttpStatus.BAD_REQUEST.value(), "이미 해당 일정에 배정된 유저가 포함되어 있습니다");
        }

        //유저 목록 조회
        List<User> userListToAssign = userRepository.findAllById(userIdListToAssign);
        if (userListToAssign.size() != userIdListToAssign.size())
            throw new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저가 포함되어 있습니다");

        //유저 할당
        userListToAssign.forEach(userToAssign -> scheduleUserRepository.save(new ScheduleUser(userToAssign, schedulePS)));

        log.info("유저 ID {}: 일정 ID {}에 유저 ID {}를 할당", userPS.getId(), schedulePS.getId(), userIdListToAssign);

        return new UserAssignRespDto(schedulePS, userListToAssign);
    }

    //최대 인원으로 배정되었는지 검사
    private void validateScheduleUserLimit(Schedule schedule, int newAssignUserCnt) {
        int currentAssignUserCnt = scheduleUserRepository.countScheduleUserBySchedule(schedule);

        if (currentAssignUserCnt + newAssignUserCnt > 5)
            throw new CustomApiException(HttpStatus.BAD_REQUEST.value(), "해당 일정에는 더 이상 유저를 배정할 수 없습니다");
    }

}
