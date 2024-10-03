package com.my.memo.service;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import com.my.memo.util.ConnectionUtil;
import com.my.memo.util.CustomUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static com.my.memo.dto.schedule.ReqDto.*;
import static com.my.memo.dto.schedule.RespDto.*;
/**
 * 일정 관련 서비스 클래스입니다
 *
 * 일정 생성, 수정, 삭제, 조회 등의 비즈니스 로직을 처리합니다
 * 트랜잭션 관리와 예외 처리를 직접 처리하며, 공개 일정에 대한 접근 권한을 확인하는 로직을 포함하고 있습니다
 */
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ConnectionUtil connectionUtil;
    private final Logger log = LoggerFactory.getLogger(ScheduleService.class);


    /**
     * 필터링된 공개 일정 목록을 조회합니다
     *
     * 페이지네이션과 필터링(수정일, 작성자명 등)이 적용된 공개 일정을 조회합니다
     *
     * @param page 페이지 번호
     * @param limit 페이지당 일정 수
     * @param modifiedAt 수정일 필터
     * @param authorName 작성자명 필터
     * @param startModifiedAt 수정일 범위의 시작 날짜
     * @param endModifiedAt 수정일 범위의 종료 날짜
     * @return 조회된 일정 목록과 다음 페이지 존재 여부가 담긴 DTO
     */
    public ScheduleListRespDto findPublicSchedulesWithFilters(long page, long limit, String modifiedAt, String authorName, String startModifiedAt, String endModifiedAt){

        try (Connection connection = connectionUtil.getConnectionForReadOnly()) {
            long offset = page * limit;

            List<Schedule> scheduleList = scheduleRepository.findPublicSchedulesWithFilters(connection, limit + 1, offset, modifiedAt, authorName, startModifiedAt, endModifiedAt);

            boolean hasNextPage = false;

            if (scheduleList.size() > limit) {
                hasNextPage = true;
                scheduleList = scheduleList.subList(0, (int) limit);
            }

            return new ScheduleListRespDto(scheduleList, hasNextPage);
        } catch (SQLException e) {
            log.error("전체 공개 일정 조회 중 오류 발생", e);
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "전체 공개 일정 조회 중 오류 발생");
        }
    }


    /**
     * 일정 삭제를 처리합니다
     *
     * 사용자가 소유한 일정만 삭제할 수 있으며, 권한이 없을 경우 예외가 발생합니다
     * 트랜잭션을 통해 삭제 작업이 이루어지며, 예외 발생 시 롤백됩니다
     *
     * @param scheduleId 삭제할 일정의 ID
     * @param session 현재 사용자의 세션
     * @return 삭제된 여부가 담긴 응답 DTO
     */
    public ScheduleDeleteRespDto deleteSchedule(Long scheduleId, HttpSession session) {
        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("유저 ID: {}", userId);

        if (userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;

        try {
            connection = connectionUtil.getConnectionForTransaction();

            // 일정 조회
            Schedule schedule = scheduleRepository.findById(connection, scheduleId).orElseThrow(
                    () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
            );

            // 유저가 소유한 일정인지 확인
            if (!schedule.getUser().getId().equals(userId)) {
                throw new CustomApiException(HttpStatus.FORBIDDEN.value(), "해당 일정에 대한 권한이 없습니다");
            }

            // 일정 삭제
            scheduleRepository.deleteById(connection, scheduleId);

            // 트랜잭션 커밋
            connectionUtil.commit(connection);

            return new ScheduleDeleteRespDto(scheduleId, true);

        } catch (SQLException e) {
            connectionUtil.rollback(connection);
            log.error("일정 삭제 중 오류 발생", e);
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "일정 삭제 중 오류 발생");
        }finally {
            connectionUtil.close(connection);
        }
    }



    /**
     * 일정을 수정합니다
     *
     * 사용자가 소유한 일정만 수정할 수 있으며, 트랜잭션을 통해 수정 작업이 이루어집니다
     * 예외 발생 시 트랜잭션 롤백됩니다
     *
     * @param scheduleModifyReqDto 수정할 일정 정보를 담은 DTO
     * @param scheduleId 수정할 일정의 ID
     * @param session 현재 사용자의 세션
     * @return 수정된 일정 정보를 담은 응답 DTO
     */
    public ScheduleModifyRespDto updateSchedule(ScheduleModifyReqDto scheduleModifyReqDto, Long scheduleId , HttpSession session){
        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("유저 ID: {}", userId);

        if(userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;

        try {
            connection = connectionUtil.getConnectionForTransaction();

            // 해당 일정 조회
            Schedule schedulePS = scheduleRepository.findById(connection, scheduleId).orElseThrow(
                    () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
            );

            // 작성자와 일치하는지 확인
            if (!schedulePS.getUser().getId().equals(userId)) {
                throw new CustomApiException(HttpStatus.FORBIDDEN.value(), "해당 일정에 대한 권한이 없습니다");
            }

            // 요청한 필드에 대해 수정
            schedulePS.modify(scheduleModifyReqDto);

            // DB 업데이트
            scheduleRepository.update(connection, scheduleId, scheduleModifyReqDto);

            // 트랜잭션 커밋
            connectionUtil.commit(connection);

            return new ScheduleModifyRespDto(schedulePS);

        } catch (SQLException e) { //db 에러
            connectionUtil.rollback(connection);
            log.error("일정 수정 중 오류 발생", e);
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "일정 수정 중 오류 발생");
        }finally {
            connectionUtil.close(connection);
        }
    }


    /**
     * 해당 사용자가 등록한 모든 일정을 페이지네이션하여 조회합니다
     *
     * 일정 수정일과 기타 필터를 적용하여 일정 목록을 조회하며, 페이징하여 반환합니다
     *
     * @param session 현재 사용자의 세션
     * @param page 페이지 번호
     * @param limit 페이지당 일정 수
     * @param modifiedAt 수정일 필터
     * @param startModifiedAt 수정일 범위의 시작 날짜
     * @param endModifiedAt 수정일 범위의 종료 날짜
     * @return 조회된 일정 목록과 다음 페이지 존재 여부가 담긴 응답 DTO
     */
    public UserScheduleListRespDto findUserSchedules(HttpSession session, long page, long limit, String modifiedAt, String startModifiedAt, String endModifiedAt){
        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");

        if(userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        try (Connection connection = connectionUtil.getConnectionForReadOnly()) {
            long offset = page * limit;

            List<Schedule> scheduleList = scheduleRepository.findAllByUserIdWithPagination(connection, userId, limit + 1, offset, modifiedAt, startModifiedAt, endModifiedAt);

            boolean hasNextPage = false;

            // 만약 가져온 스케줄의 수가 limit을 초과하면 -> 다음 페이지 있음
            if (scheduleList.size() > limit) {
                hasNextPage = true;
                scheduleList = scheduleList.subList(0, (int) limit);  // 현재 페이지에 필요한 데이터만 남김
            }

            log.info("유저 전체 일정 조회 완료: 유저 ID {}", userId);
            return new UserScheduleListRespDto(scheduleList, hasNextPage);
        } catch (SQLException e){
            log.error("사용자 전체 일정 조회 중 오류 발생", e);
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용자 전체 일정 조회 중 오류 발생");
        }
    }


    /**
     * 선택한 일정을 조회합니다
     *
     * 세션에 유저 정보가 없을 경우, 일정의 공개 여부를 확인하여 반환합니다
     *
     * @param scheduleId 조회할 일정의 ID
     * @param session 현재 사용자의 세션
     * @return 조회된 일정 정보를 담은 응답 DTO
     */
    public ScheduleRespDto findUserScheduleById(Long scheduleId, HttpSession session){

        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");

        try (Connection connection = connectionUtil.getConnectionForReadOnly()) {
            // 해당 일정 조회
            Schedule schedulePS = scheduleRepository.findById(connection, scheduleId).orElseThrow(
                    () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
            );

            // 비공개 일정인데 해당 유저의 일정이 아니면 에러
            if (!schedulePS.isPublic() && (userId == null || !schedulePS.getUser().getId().equals(userId))) {
                throw new CustomApiException(HttpStatus.FORBIDDEN.value(), "해당 일정에 접근할 권한이 없습니다");
            }

            log.info("선택한 일정 조회 완료: 유저 ID {}, 일정 ID {}", userId, scheduleId);
            return new ScheduleRespDto(schedulePS);
        } catch (SQLException e) {
            log.error("선택한 일정 조회 중 오류 발생", e);
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "선택한 일정 조회 중 오류 발생");
        }
    }



    /**
     * 새로운 일정을 생성합니다
     *
     * 세션에서 유저 정보를 가져와 해당 유저에 대한 새로운 일정을 생성합니다
     * 트랜잭션을 통해 생성 작업이 이루어지며, 예외 발생 시 롤백됩니다
     *
     * @param scheduleCreateReqDto 생성할 일정 정보를 담은 DTO
     * @param session 현재 사용자의 세션
     * @return 생성된 일정 정보가 담긴 응답 DTO
     */
    public ScheduleCreateRespDto createSchedule (ScheduleCreateReqDto scheduleCreateReqDto, HttpSession session){

        //유저 정보 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("유저 ID: {}", userId);

        if(userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;
        try{
            connection = connectionUtil.getConnectionForTransaction();

            //유저 찾기
            User userPS = userRepository.findById(connection, userId).orElseThrow(
                    () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
            );

            LocalDateTime localDateTime = LocalDateTime.now();


            Schedule schedule = Schedule.builder()
                    .startAt(CustomUtil.scheduleTimeToLocalDateTime(scheduleCreateReqDto.getStartAt()))
                    .endAt(CustomUtil.scheduleTimeToLocalDateTime(scheduleCreateReqDto.getEndAt()))
                    .createdAt(localDateTime)
                    .lastModifiedAt(localDateTime)
                    .content(scheduleCreateReqDto.getContent())
                    .user(userPS)
                    .isPublic(scheduleCreateReqDto.getIsPublic())
                    .build();

            //저장
            Long id = scheduleRepository.save(connection, schedule);

            // 트랜잭션 커밋
            connectionUtil.commit(connection);

            log.info("일정 저장 완료 : 일정 ID {}", id);
            return new ScheduleCreateRespDto(schedule, id);

        }catch (SQLException e){
            connectionUtil.rollback(connection);
            log.error("일정 생성 중 오류 발생", e);
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "일정 생성 중 오류 발생");
        } finally {
            connectionUtil.close(connection);
        }
    }




}
