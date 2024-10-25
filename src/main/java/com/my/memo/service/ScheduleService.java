package com.my.memo.service;

import com.my.memo.client.weather.WeatherClient;
import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.comment.CommentRepository;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.schedule.dto.ScheduleWithCommentAndUserCountsDto;
import com.my.memo.domain.scheduleUser.ScheduleUser;
import com.my.memo.domain.scheduleUser.ScheduleUserRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.dto.schedule.req.PublicScheduleFilter;
import com.my.memo.dto.schedule.req.ScheduleCreateReqDto;
import com.my.memo.dto.schedule.req.ScheduleModifyReqDto;
import com.my.memo.dto.schedule.req.UserScheduleFilter;
import com.my.memo.dto.schedule.resp.*;
import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.ErrorCode;
import com.my.memo.util.CustomUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ScheduleService {


    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;
    private final ScheduleUserRepository scheduleUserRepository;
    private final UserRepository userRepository;
    private final WeatherClient weatherClient;
    private final Clock clock;
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public PublicScheduleListRespDto findPublicSchedulesWithFilters(PublicScheduleFilter publicScheduleFilter) {

        List<ScheduleWithCommentAndUserCountsDto> scheduleList = scheduleRepository.findPublicSchedulesWithFilters(publicScheduleFilter);

        int totalPublicSchedules = scheduleRepository.countPublicSchedules();
        int totalPages = (int) Math.ceil((double) totalPublicSchedules / publicScheduleFilter.getLimit());
        int currentPage = publicScheduleFilter.getPage().intValue();

        boolean hasNextPage = scheduleList.size() > publicScheduleFilter.getLimit();

        if (hasNextPage) {
            scheduleList = scheduleList.subList(0, publicScheduleFilter.getLimit().intValue());
        }

        return new PublicScheduleListRespDto(scheduleList, hasNextPage, totalPublicSchedules, totalPages, currentPage);
    }


    // 일정 작성자가 누구던 간에 관리자는 해당 일정 수정/삭제 가능함
    @Transactional
    public ScheduleDeleteRespDto deleteSchedule(Long scheduleId, Long userId) {

        //관리자 검증
        userService.findByIdOrFail(userId);
        log.info("일정 삭제 시도: 관리자 ID {}", userId);

        //해당 일정 조회
        Schedule schedulePS = scheduleRepository.findScheduleWithCommentsById(scheduleId).orElseThrow(
                () -> new CustomApiException(ErrorCode.SCHEDULE_NOT_FOUND));
        //작성자 조회
        User userPS = userRepository.findUserWithSchedulesById(schedulePS.getUser().getId()).orElseThrow(
                () -> new CustomApiException(ErrorCode.AUTHOR_NOT_FOUND));

        //스케줄에 배정된 유저 리스트 삭제
        int deletedAssignedUserCnt = scheduleUserRepository.deleteByScheduleId(scheduleId);
        log.info("일정 ID {}에 배정된 유저 삭제 완료: 삭제된 개수 {}", scheduleId, deletedAssignedUserCnt);

        //해당 스케줄 삭제
        userPS.getScheduleList().remove(schedulePS);
        scheduleRepository.deleteById(scheduleId);

        return new ScheduleDeleteRespDto(scheduleId, true);
    }


    //일정 작성자가 누구던 간에 관리자는 해당 일정 수정/삭제 가능함
    @Transactional
    public ScheduleModifyRespDto updateSchedule(ScheduleModifyReqDto scheduleModifyReqDto, Long scheduleId, Long userId) {

        User userPS = userService.findByIdOrFail(userId);
        log.info("일정 수정 시도: 관리자 ID {}", userPS.getId());
        //해당 일정 조회
        Schedule schedulePS = findByIdOrFail(scheduleId);
        // 요청한 필드에 대해 수정
        schedulePS.modify(scheduleModifyReqDto);

        return new ScheduleModifyRespDto(schedulePS);
    }

    public UserScheduleListRespDto findUserSchedules(UserScheduleFilter userScheduleFilter, Long userId) {

        User userPS = userService.findByIdOrFail(userId);

        List<ScheduleWithCommentAndUserCountsDto> scheduleList = scheduleRepository.findUserSchedulesWithFilters(userPS, userScheduleFilter);

        int totalUserSchedules = scheduleRepository.countUserSchedules(userId);
        int totalPages = (int) Math.ceil((double) totalUserSchedules / userScheduleFilter.getLimit());
        int currentPage = userScheduleFilter.getPage().intValue();

        boolean hasNextPage = scheduleList.size() > userScheduleFilter.getLimit();

        // 만약 가져온 스케줄의 수가 limit을 초과하면 -> 다음 페이지 있음
        if (hasNextPage) {
            scheduleList = scheduleList.subList(0, userScheduleFilter.getLimit().intValue());  // 현재 페이지에 필요한 데이터만 남김
        }

        log.info("유저 전체 일정 조회 완료: 유저 ID {}", userPS.getId());
        return new UserScheduleListRespDto(scheduleList, hasNextPage, userPS, totalUserSchedules, totalPages, currentPage);
    }


    public ScheduleRespDto findScheduleById(Long scheduleId, int page, int limit, Long userId) {

        User userPS = userService.findByIdOrFail(userId);
        Schedule schedulePS = findByIdOrFail(scheduleId);

        validateScheduleAccess(schedulePS, userPS);

        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findCommentsWithUserBySchedule(schedulePS, pageRequest);
        List<ScheduleUser> assignedUserList = scheduleUserRepository.findScheduleUserBySchedule(schedulePS);

        return new ScheduleRespDto(schedulePS, commentPage, assignedUserList);
    }

    private void validateScheduleAccess(Schedule schedule, User user) {
        if (!schedule.isPublic() && !user.isAdmin() && !schedule.isOwner(user)) {
            throw new CustomApiException(ErrorCode.FORBIDDEN_SCHEDULE_ACCESS);
        }
    }

    @Transactional
    public ScheduleCreateRespDto createSchedule(ScheduleCreateReqDto scheduleCreateReqDto, Long userId) {

        User userPS = userService.findByIdOrFail(userId);
        String todayWeather = weatherClient.getTodayWeather(CustomUtil.localDateTimeToFormattedString(LocalDateTime.now(clock)));
        Schedule schedulePS = scheduleRepository.save(scheduleCreateReqDto.toEntity(userPS, todayWeather));

        log.info("일정 저장 완료 : 일정 ID {}, 유저 ID {}", schedulePS.getId(), userPS.getId());
        return new ScheduleCreateRespDto(schedulePS);
    }

    public Schedule findByIdOrFail(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomApiException(ErrorCode.SCHEDULE_NOT_FOUND)
        );
    }

}
