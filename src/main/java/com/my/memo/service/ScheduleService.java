package com.my.memo.service;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
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

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final DataSource dataSource;
    private final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    //공개 일정 단건 조회
    // isPublic = true인 것들만 대상
    public ScheduleRespDto findPublicScheduleById(Long scheduleId){
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            //해당 일정 조회
            Schedule schedulePS = scheduleRepository.findById(connection, scheduleId).orElseThrow(
                    () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
            );

            if(!schedulePS.isPublic())
                throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "접근할 수 없는 일정입니다");

            return new ScheduleRespDto(schedulePS);
        }catch (SQLException e){
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "일정 조회 중 오류 발생");
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    log.error("커넥션 닫기 중 오류 발생: " + closeEx.getMessage());
                }
            }
        }
    }


    // 메인 페이지에서 일정 검색 (필터: 수정일, 작성자명) -> 세션 검사 필요 없음
    // isPublic = true인 것들만 대상
    public ScheduleListRespDto findPublicSchedulesWithFilters(long page, long limit, String modifiedAt, String authorName, String startModifiedAt, String endModifiedAt){

        Connection connection = null;
        try{
            connection = dataSource.getConnection();

            long offset = page * limit;

            List<Schedule> scheduleList = scheduleRepository.findPublicSchedulesWithFilters(connection, limit+1, offset, modifiedAt, authorName, startModifiedAt, endModifiedAt);

            boolean hasNextPage = false;

            if (scheduleList.size() > limit) {
                hasNextPage = true;
                scheduleList = scheduleList.subList(0, (int) limit);
            }

            return new ScheduleListRespDto(scheduleList, hasNextPage);
        }catch (SQLException e){
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    log.error("커넥션 닫기 중 오류 발생: " + closeEx.getMessage());
                }
            }
        }
    }


    // 일정 삭제
    public ScheduleDeleteRespDto deleteSchedule(Long scheduleId, HttpSession session) {
        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("유저 ID: {}", userId);

        if (userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            //트랜잭션 시작
            connection.setAutoCommit(false);

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
            connection.commit();

            return new ScheduleDeleteRespDto(scheduleId, true);

        } catch (SQLException e) {
            try {
                // 트랜잭션 롤백
                connection.rollback();
                log.info("트랜잭션 롤백 완료");
            } catch (SQLException rollbackEx) {
                log.error("롤백 중 오류 발생: " + rollbackEx.getMessage());
            }
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "일정 삭제 중 오류 발생");
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    log.error("커넥션 닫기 중 오류 발생: " + closeEx.getMessage());
                }
            }
        }
    }





    //할 일 수정 (내용, 시작 시간, 종료 시간, 공개 여부)
    public ScheduleModifyRespDto updateSchedule(ScheduleModifyReqDto scheduleModifyReqDto, Long scheduleId , HttpSession session){
        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("유저 ID: {}", userId);

        if(userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;


        try {
            connection = dataSource.getConnection();
            // 트랜잭션 시작
            connection.setAutoCommit(false);

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
            connection.commit();

            return new ScheduleModifyRespDto(schedulePS);

        } catch (SQLException e) { //db 에러
            try {
                // 트랜잭션 롤백
                connection.rollback();
                log.info("트랜잭션 롤백 완료");
            } catch (SQLException rollbackEx) {
                log.error("롤백 중 오류 발생: " + rollbackEx.getMessage());
            }
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    log.error("커넥션 닫기 중 오류 발생: " + closeEx.getMessage());
                }
            }
        }
    }


    //해당 유저가 등록한 전체 일정 조회
    /*
     * 100 개의 데이터
     * limit = 10이라 하면
     *
     * 처음 0 페이지 -> 10개 내보냄 (1~10)
     * 처음 1 페이지 -> 10개 내보냄 (11~20)
     * 처음 2 페이지 -> 10개 내보냄 (21~30)
     *
     * */
    public UserScheduleListRespDto findUserSchedules(HttpSession session, long page, long limit, String modifiedAt, String startModifiedAt, String endModifiedAt){
        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("유저 ID: {}", userId);

        if(userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;

        try{
            connection = dataSource.getConnection();

            long offset = page * limit;

            List<Schedule> scheduleList = scheduleRepository.findAllByUserIdWithPagination(connection, userId, limit+1, offset, modifiedAt, startModifiedAt, endModifiedAt);

            boolean hasNextPage = false;

            // 만약 가져온 스케줄의 수가 limit을 초과하면 -> 다음 페이지 있음
            if (scheduleList.size() > limit) {
                hasNextPage = true;
                scheduleList = scheduleList.subList(0, (int) limit);  // 현재 페이지에 필요한 데이터만 남김
            }

            return new UserScheduleListRespDto(scheduleList, hasNextPage);
        } catch (SQLException e){
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    log.error("커넥션 닫기 중 오류 발생: " + closeEx.getMessage());
                }
            }
        }
    }


    //선택한 일정 조회 (세션에 유저 정보 없으면 -> 공개 일정 여부 검증 후 반환)
    public ScheduleRespDto findUserScheduleById(Long scheduleId, HttpSession session){

        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");

        Connection connection = null;
        try{
            connection = dataSource.getConnection();

            //해당 일정 조회
            Schedule schedulePS = scheduleRepository.findById(connection, scheduleId).orElseThrow(
                    () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
            );

            //비공개 일정인데 해당 유저의 일정이 아니면 에러
            if(!schedulePS.isPublic() && !schedulePS.getUser().getId().equals(userId))
                throw new CustomApiException(HttpStatus.FORBIDDEN.value(), "해당 일정에 접근할 권한이 없습니다");

            return new ScheduleRespDto(schedulePS);
        }catch (SQLException e){
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "일정 조회 중 오류 발생");
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    log.error("커넥션 닫기 중 오류 발생: " + closeEx.getMessage());
                }
            }
        }
    }



    //스케줄 생성
    public ScheduleCreateRespDto createSchedule (ScheduleCreateReqDto scheduleCreateReqDto, HttpSession session){

        //유저 정보 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("유저 ID: {}", userId);

        if(userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
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
            connection.commit();

            log.info("일정 저장 완료 : {}", id);
            return new ScheduleCreateRespDto(schedule, id);

        }catch (SQLException e){
            if (connection != null) {
                try {
                    connection.rollback(); // 롤백
                    log.info("트랜잭션 롤백 완료");
                } catch (SQLException rollbackEx) {
                    log.error("롤백 중 오류 발생: " + rollbackEx.getMessage());
                }
            }
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close(); // 연결 해제
                } catch (SQLException closeEx) {
                    log.error("커넥션 닫기 중 오류 발생: " + closeEx.getMessage());
                }
            }
        }
    }




}
