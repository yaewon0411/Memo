package com.my.memo.service;

import com.my.memo.aop.valid.RequireAuthenticatedUser;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.scheduleUser.ScheduleUser;
import com.my.memo.domain.scheduleUser.ScheduleUserRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.dto.scheduleUser.req.AssignedUserDeleteReqDto;
import com.my.memo.dto.scheduleUser.req.UserAssignReqDto;
import com.my.memo.dto.scheduleUser.resp.AssignedUserDeleteRespDto;
import com.my.memo.dto.scheduleUser.resp.UserAssignRespDto;
import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleUserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final ScheduleUserRepository scheduleUserRepository;
    private final UserService userService;
    private final ScheduleService scheduleService;


    @Transactional
    @RequireAuthenticatedUser
    public AssignedUserDeleteRespDto deleteAssignedUser(Long scheduleId, AssignedUserDeleteReqDto assignedUserDeleteReqDto, Long userId) {

        User userPS = userService.findByIdOrFail(userId);
        Schedule schedulePS = scheduleService.findByIdOrFail(scheduleId);
        //일정 접근 권한 검사
        schedulePS.validateScheduleAccess(userPS);

        //일정에서 삭제할 유저 아이디 리스트
        List<Long> userIdListToDelete = assignedUserDeleteReqDto.getUserIdList().stream().map(AssignedUserDeleteReqDto.UserDto::getUserId).toList();
        //실제로 해당 일정에 배정되어 있는 유저인지 검증
        if (!scheduleUserRepository.getAssignedUserIdsBySchedule(schedulePS).containsAll(userIdListToDelete)) {
            throw new CustomApiException(ErrorCode.USER_NOT_ASSIGNED_TO_SCHEDULE);
        }
        int deletedCnt = scheduleUserRepository.deleteByUserIdsAndSchedule(
                userIdListToDelete,
                schedulePS);

        return new AssignedUserDeleteRespDto(true, schedulePS, deletedCnt);
    }

    @Transactional
    @RequireAuthenticatedUser
    public UserAssignRespDto assignUserToSchedule(Long scheduleId, UserAssignReqDto userAssignReqDto, Long userId) {

        User userPS = userService.findByIdOrFail(userId);
        Schedule schedulePS = scheduleService.findByIdOrFail(scheduleId);
        //일정 접근 권한 검사
        schedulePS.validateScheduleAccess(userPS);
        //최대 인원 검사
        validateScheduleUserLimit(schedulePS, userAssignReqDto);

        List<Long> userIdListToAssign = userAssignReqDto.getUserIdList().stream()
                .map(UserAssignReqDto.UserDto::getUserId)
                .toList();

        //이미 일정에 배정된 유저인지 검사
        Set<Long> assignedUserIdSet = scheduleUserRepository.getAssignedUserIdsBySchedule(schedulePS);
        validateUsersNotAlreadyAssigned(assignedUserIdSet, userIdListToAssign);

        //유저 목록 조회
        List<User> userListToAssign = userRepository.findAllById(userIdListToAssign);
        if (userListToAssign.size() != userIdListToAssign.size()) {
            throw new CustomApiException(ErrorCode.USER_NOT_EXIST);
        }
        //유저 할당
        userListToAssign.forEach(userToAssign -> scheduleUserRepository.save(new ScheduleUser(userToAssign, schedulePS)));
        log.info("유저 ID {}: 일정 ID {}에 유저 ID {}를 할당", userPS.getId(), schedulePS.getId(), userIdListToAssign);

        return new UserAssignRespDto(schedulePS, userListToAssign);
    }

    //최대 인원으로 배정되었는지 검사
    private void validateScheduleUserLimit(Schedule schedule, UserAssignReqDto userAssignReqDto) {
        int newAssignUserCnt = userAssignReqDto.getUserIdList().size();
        int currentAssignUserCnt = scheduleUserRepository.countScheduleUserBySchedule(schedule);

        if (currentAssignUserCnt + newAssignUserCnt > 5)
            throw new CustomApiException(ErrorCode.SCHEDULE_USER_LIMIT_EXCEEDED);
    }

    private void validateUsersNotAlreadyAssigned(Set<Long> assignedUserIdSet, List<Long> userIdListToAssign) {
        for (Long userIdToAssign : userIdListToAssign) {
            if (assignedUserIdSet.contains(userIdToAssign)) {
                throw new CustomApiException(ErrorCode.USER_ALREADY_ASSIGNED_TO_SCHEDULE);
            }
        }
    }

}
