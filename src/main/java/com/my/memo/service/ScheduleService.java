package com.my.memo.service;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
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

import java.util.List;

import static com.my.memo.dto.schedule.ReqDto.ScheduleCreateReqDto;
import static com.my.memo.dto.schedule.ReqDto.ScheduleModifyReqDto;
import static com.my.memo.dto.schedule.RespDto.*;

/**
 * 일정 관련 서비스 클래스입니다
 * <p>
 * 일정 생성, 수정, 삭제, 조회 등의 비즈니스 로직을 처리합니다
 * 트랜잭션 관리와 예외 처리를 직접 처리하며, 공개 일정에 대한 접근 권한을 확인하는 로직을 포함하고 있습니다
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ScheduleService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final Logger log = LoggerFactory.getLogger(ScheduleService.class);


    /**
     * 필터링된 공개 일정 목록을 조회합니다
     * <p>
     * 페이지네이션과 필터링(수정일, 작성자명 등)이 적용된 공개 일정을 조회합니다
     *
     * @param page            페이지 번호
     * @param limit           페이지당 일정 수
     * @param modifiedAt      수정일 필터
     * @param authorName      작성자명 필터
     * @param startModifiedAt 수정일 범위의 시작 날짜
     * @param endModifiedAt   수정일 범위의 종료 날짜
     * @return 조회된 일정 목록과 다음 페이지 존재 여부가 담긴 DTO
     */
    //TODO 테스트
    public ScheduleListRespDto findPublicSchedulesWithFilters(long page, long limit, String modifiedAt, String authorName, String startModifiedAt, String endModifiedAt) {

        List<Schedule> scheduleList = scheduleRepository.findPublicSchedulesWithFilters(limit, page, modifiedAt, authorName, startModifiedAt, endModifiedAt);
        scheduleRepository.getCommentsBySchedules(scheduleList);

        boolean hasNextPage = false;

        if (scheduleList.size() > limit) {
            hasNextPage = true;
            scheduleList = scheduleList.subList(0, (int) limit);
        }

        return new ScheduleListRespDto(scheduleList, hasNextPage);
    }


    //TODO 고아객체 삭제되는지 테스트
    //TODO 나중에 코멘트랑 ScheduleUser도 삭제되도록 추가해야 함!!!
    @Transactional
    public ScheduleDeleteRespDto deleteSchedule(Long scheduleId, HttpServletRequest request) {
        //유저 꺼내기
        Long userId = (Long) request.getAttribute("userId");
        log.info("유저 ID: {}", userId);

        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
        );

        // 해당 일정 조회
        Schedule schedulePS = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
        );

        // 일정 작성자가 누구던 간에 관리자는 해당 일정 수정/삭제 가능함

        userPS.getScheduleList().remove(schedulePS);

        return new ScheduleDeleteRespDto(scheduleId, true);
    }


    @Transactional
    public ScheduleModifyRespDto updateSchedule(ScheduleModifyReqDto scheduleModifyReqDto, Long scheduleId, HttpServletRequest request) {
        //유저 꺼내기
        Long userId = (Long) request.getAttribute("userId");
        log.info("유저 ID: {}", userId);

        userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
        );

        // 해당 일정 조회
        Schedule schedulePS = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
        );

        //일정 작성자가 누구던 간에 관리자는 해당 일정 수정/삭제 가능함

        // 요청한 필드에 대해 수정
        schedulePS.modify(scheduleModifyReqDto);

        return new ScheduleModifyRespDto(schedulePS);
    }


    public UserScheduleListRespDto findUserSchedules(HttpServletRequest request, long page, long limit, String modifiedAt, String startModifiedAt, String endModifiedAt) {
        //유저 꺼내기
        Long userId = (Long) request.getAttribute("userId");

        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
        );

        List<Schedule> scheduleList = scheduleRepository.findAllByUserIdWithPagination(userPS, limit, page, modifiedAt, startModifiedAt, endModifiedAt);
        scheduleRepository.getCommentsBySchedules(scheduleList);

        boolean hasNextPage = false;

        // 만약 가져온 스케줄의 수가 limit을 초과하면 -> 다음 페이지 있음
        if (scheduleList.size() > limit) {
            hasNextPage = true;
            scheduleList = scheduleList.subList(0, (int) limit);  // 현재 페이지에 필요한 데이터만 남김
        }

        log.info("유저 전체 일정 조회 완료: 유저 ID {}", userId);
        return new UserScheduleListRespDto(scheduleList, hasNextPage);
    }


    public ScheduleRespDto findUserScheduleById(Long scheduleId, HttpServletRequest request) {

        //유저 꺼내기
        Long userId = (Long) request.getAttribute("userId");

        Schedule schedulePS = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
        );

        // 비공개 일정인데 해당 유저의 일정이 아니면 에러
        if (!schedulePS.isPublic() && !schedulePS.getUser().getId().equals(userId)) {
            throw new CustomApiException(HttpStatus.FORBIDDEN.value(), "해당 일정에 접근할 권한이 없습니다");
        }

        log.info("선택한 일정 조회 완료: 유저 ID {}, 일정 ID {}", userId, scheduleId);
        return new ScheduleRespDto(schedulePS);
    }


    @Transactional
    public ScheduleCreateRespDto createSchedule(ScheduleCreateReqDto scheduleCreateReqDto, HttpServletRequest request) {

        //유저 정보 꺼내기
        Long authUserId = (Long) request.getAttribute("userId");
        log.info("유저 ID: {}", authUserId);

        User userPS = userRepository.findById(authUserId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
        );

        Schedule schedule = Schedule.builder()
                .startAt(scheduleCreateReqDto.getStartAt())
                .endAt(scheduleCreateReqDto.getEndAt())
                .content(scheduleCreateReqDto.getContent())
                .user(userPS)
                .isPublic(scheduleCreateReqDto.getIsPublic())
                .build();

        Schedule schedulePS = scheduleRepository.save(schedule);

        log.info("일정 저장 완료 : 일정 ID {}", schedulePS.getId());
        return new ScheduleCreateRespDto(schedulePS);
    }


}
