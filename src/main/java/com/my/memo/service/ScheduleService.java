package com.my.memo.service;

import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.comment.CommentRepository;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.schedule.dto.ScheduleWithCommentAndUserCountsDto;
import com.my.memo.domain.scheduleUser.ScheduleUser;
import com.my.memo.domain.scheduleUser.ScheduleUserRepository;
import com.my.memo.domain.user.Role;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import com.my.memo.feign.WeatherService;
import com.my.memo.util.CustomUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.my.memo.dto.schedule.ReqDto.*;
import static com.my.memo.dto.schedule.RespDto.*;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ScheduleService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;
    private final ScheduleUserRepository scheduleUserRepository;
    private final WeatherService weatherService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public PublicScheduleListRespDto findPublicSchedulesWithFilters(PublicScheduleFilter publicScheduleFilter) {

        List<ScheduleWithCommentAndUserCountsDto> scheduleList = scheduleRepository.findPublicSchedulesWithFilters(publicScheduleFilter);

        boolean hasNextPage = false;

        if (scheduleList.size() > publicScheduleFilter.getLimit()) {
            hasNextPage = true;
            scheduleList = scheduleList.subList(0, (int) publicScheduleFilter.getLimit().longValue());
        }

        return new PublicScheduleListRespDto(scheduleList, hasNextPage);
    }


    // 일정 작성자가 누구던 간에 관리자는 해당 일정 수정/삭제 가능함
    @Transactional
    public ScheduleDeleteRespDto deleteSchedule(Long scheduleId, HttpServletRequest request) {
        User userPS = validateAndGetUser(request);

        log.info("일정 삭제 시도: 관리자 ID {}", userPS.getId());

        //해당 일정 조회
        Schedule schedulePS = validateAndGetSchedule(scheduleId);

        //코멘트 삭제
        int deletedCommentCnt = commentRepository.deleteBySchedule(schedulePS);
        log.info("일정 ID {}에 달린 코멘트 삭제 완료: 삭제된 개수 {}", scheduleId, deletedCommentCnt);

        //스케줄에 배정된 유저 리스트 삭제
        int deletedAssignedUserCnt = scheduleUserRepository.deleteBySchedule(schedulePS);
        log.info("일정 ID {}에 배정된 유저 삭제 완료: 삭제된 개수 {}", scheduleId, deletedAssignedUserCnt);

        //해당 스케줄 삭제
        schedulePS.getUser().getScheduleList().remove(schedulePS);

        //TODO 하이버네이트 버그로 인해 Cascade.PERSIS와 orphanRemoval=true가 같이 쓰일 때만 고아 객체로 인식되어 제거된다함. 명시적 삭제 필요
        scheduleRepository.delete(schedulePS);

        return new ScheduleDeleteRespDto(scheduleId, true);
    }


    //일정 작성자가 누구던 간에 관리자는 해당 일정 수정/삭제 가능함
    @Transactional
    public ScheduleModifyRespDto updateSchedule(ScheduleModifyReqDto scheduleModifyReqDto, Long scheduleId, HttpServletRequest request) {
        User userPS = validateAndGetUser(request);

        log.info("일정 수정 시도: 관리자 ID {}", userPS.getId());

        //해당 일정 조회
        Schedule schedulePS = validateAndGetSchedule(scheduleId);

        // 요청한 필드에 대해 수정
        schedulePS.modify(scheduleModifyReqDto);

        return new ScheduleModifyRespDto(schedulePS);
    }

    public UserScheduleListRespDto findUserSchedules(UserScheduleFilter userScheduleFilter, HttpServletRequest request) {
        User userPS = validateAndGetUser(request);

        List<ScheduleWithCommentAndUserCountsDto> scheduleList = scheduleRepository.findUserSchedulesWithFilters(userPS, userScheduleFilter);

        boolean hasNextPage = false;

        // 만약 가져온 스케줄의 수가 limit을 초과하면 -> 다음 페이지 있음
        if (scheduleList.size() > userScheduleFilter.getLimit()) {
            hasNextPage = true;
            scheduleList = scheduleList.subList(0, (int) userScheduleFilter.getLimit().longValue());  // 현재 페이지에 필요한 데이터만 남김
        }

        log.info("유저 전체 일정 조회 완료: 유저 ID {}", userPS.getId());
        return new UserScheduleListRespDto(scheduleList, hasNextPage, userPS);
    }


    public ScheduleRespDto findScheduleById(Long scheduleId, int page, int limit, HttpServletRequest request) {
        User userPS = validateAndGetUser(request);

        Schedule schedulePS = validateAndGetSchedule(scheduleId);

        if (!schedulePS.isPublic() && !userPS.getRole().equals(Role.ADMIN)) {
            if (!schedulePS.getUser().equals(userPS)) {
                throw new CustomApiException(HttpStatus.FORBIDDEN.value(), "해당 일정에 접근할 권한이 없습니다");
            }
        }

        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "createdAt"));

        Page<Comment> commentPage = commentRepository.findCommentsWithUserBySchedule(schedulePS, pageRequest);
        commentPage.getContent().forEach(c -> c.getUser().getName());

        List<ScheduleUser> assignedUserList = scheduleUserRepository.findScheduleUserBySchedule(schedulePS);

        return new ScheduleRespDto(schedulePS, commentPage, assignedUserList);
    }


    @Transactional
    public ScheduleCreateRespDto createSchedule(ScheduleCreateReqDto scheduleCreateReqDto, HttpServletRequest request) {

        User userPS = validateAndGetUser(request);

        String todayWeather = weatherService.getTodayWeather(CustomUtil.localDateTimeToFormattedString(LocalDateTime.now()));

        Schedule schedulePS = scheduleRepository.save(scheduleCreateReqDto.toEntity(userPS, todayWeather));

        log.info("일정 저장 완료 : 일정 ID {}, 유저 ID {}", schedulePS.getId(), userPS.getId());
        return new ScheduleCreateRespDto(schedulePS);
    }

    private User validateAndGetUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "재로그인이 필요합니다");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 유저 접근 시도: ID {}", userId);
                    return new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다");
                });
    }

    private Schedule validateAndGetSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
        );
    }


}
