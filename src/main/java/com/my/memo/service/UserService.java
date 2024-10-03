package com.my.memo.service;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import com.my.memo.util.CustomPasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static com.my.memo.dto.user.ReqDto.*;
import static com.my.memo.dto.user.RespDto.*;
/**
 * 유저 관련 서비스 클래스입니다
 *
 * 로그인, 로그아웃, 회원가입, 정보 조회, 정보 수정, 유저 삭제와 관련된 비즈니스 로직을 처리합니다
 * 트랜잭션 관리와 예외 처리를 직접 처리합니다
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * 현재 사용자를 로그아웃 처리합니다
     *
     * @param session 현재 사용자의 세션
     * @return 로그아웃 성공 여부 및 유저 ID를 포함한 응답 DTO
     */
    public UserLogoutRespDto logout(HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        session.invalidate();
        log.info("로그아웃 완료: 유저 ID {}", userId);
        return new UserLogoutRespDto(true, userId);
    }


    /**
     * 현재 사용자의 계정을 삭제합니다
     *
     * 유저와 관련된 스케줄도 함께 삭제되며, 예외 발생 시 롤백됩니다
     *
     * @param session 현재 사용자의 세션
     * @return 삭제 작업의 성공 여부 및 유저 ID를 포함한 응답 DTO
     */
    public UserDeleteRespDto deleteUser(HttpSession session) {
        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("정보 조회 유저 ID: {}", userId);

        if (userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            User userPS = userRepository.findById(connection, userId).orElseThrow(
                    () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
            );

            //스케줄 삭제
            scheduleRepository.deleteByUser(connection, userPS);

            //유저 삭제
            userRepository.delete(connection, userPS);
            connection.commit();

            return new UserDeleteRespDto(true, userId);

        } catch (SQLException e) {
            try {
                // 트랜잭션 롤백
                connection.rollback();
            } catch (SQLException rollbackEx) {
                log.error("롤백 중 오류 발생: " + rollbackEx.getMessage());
            }
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "유저 정보 수정 중 오류 발생");
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



    /**
     * 현재 사용자의 정보를 조회합니다
     *
     * @param session 현재 사용자의 세션
     * @return 유저 정보를 포함한 응답 DTO
     */
    public UserRespDto getUserInfo(HttpSession session){
        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("정보 조회 유저 ID: {}", userId);

        if (userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;
        try{

            connection = dataSource.getConnection();

            //유저 찾기
            User userPS = userRepository.findById(connection, userId).orElseThrow(
                    () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
            );

            return new UserRespDto(userPS);

        }catch (SQLException e){
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "유저 정보 조회 중 오류 발생");
        } finally {
            if(connection != null){
                try{
                    connection.close();
                } catch (SQLException closeEx) {
                    log.error("커넥션 닫기 중 오류 발생: " + closeEx.getMessage());
                }
            }
        }
    }


    /**
     * 현재 사용자의 정보를 수정합니다
     *
     * 이메일 수정 시 중복 체크 후, 유저 정보를 업데이트하고 트랜잭션을 통해 처리합니다
     * 예외 발생 시 트랜잭션 롤백합니다
     *
     * @param userModifyReqDto 수정할 유저 정보를 담은 DTO
     * @param session 현재 사용자의 세션
     * @return 수정된 유저 정보를 포함한 응답 DTO
     */
    public UserModifyRespDto updateUser(UserModifyReqDto userModifyReqDto, HttpSession session){

        //유저 꺼내기
        Long userId = (Long) session.getAttribute("userId");
        log.info("정보 수정 시도 유저 ID: {}", userId);

        if (userId == null)
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");

        Connection connection = null;

        try{

            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            //유저 찾기
            User userPS = userRepository.findById(connection, userId).orElseThrow(
                    () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
            );

            //수정 요청한 이메일이 사용중인 이메일인지 검사
            if(userModifyReqDto.getEmail() != null && !userPS.getEmail().equals(userModifyReqDto.getEmail())){
                Optional<User> userOP = userRepository.findByEmail(connection, userModifyReqDto.getEmail());
                if(userOP.isPresent())
                    throw new CustomApiException(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다");
            }

            //db 업데이트
            userRepository.update(connection, userModifyReqDto, userId);

            //수정
            userPS.modify(userModifyReqDto);
            connection.commit();

            return new UserModifyRespDto(userPS);

        }catch (SQLException e){
            try {
                // 트랜잭션 롤백
                connection.rollback();
            } catch (SQLException rollbackEx) {
                log.error("롤백 중 오류 발생: " + rollbackEx.getMessage());
            }
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "유저 정보 수정 중 오류 발생");
        }finally {
            if(connection != null){
                try{
                    connection.close();
                } catch (SQLException closeEx) {
                    log.error("커넥션 닫기 중 오류 발생: " + closeEx.getMessage());
                }
            }
        }
    }


    /**
     * 회원가입을 처리합니다
     *
     * 이메일 중복을 확인한 후, 새로운 유저를 저장합니다
     * 트랜잭션을 통해 처리되며, 예외 발생 시 롤백됩니다
     *
     * @param joinReqDto 회원가입 정보를 담은 DTO
     * @return 생성된 유저 정보를 포함한 응답 DTO
     */
    public JoinRespDto join(JoinReqDto joinReqDto){

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            //이메일 중복 검사
            if(userRepository.existsByEmail(connection, joinReqDto.getEmail())) {
                log.warn("이메일 중복: {}", joinReqDto.getEmail());
                throw new CustomApiException(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다");
            }

            //저장
            User user = joinReqDto.toEntity();


            Long userId = userRepository.save(connection, user);
            connection.commit();

            log.info("회원가입 완료: id = {}", userId);

            //반환
            return new JoinRespDto(userId, user);
        }catch (SQLException e){
            try {
                // 트랜잭션 롤백
                connection.rollback();
                log.info("트랜잭션 롤백 완료");
            } catch (SQLException rollbackEx) {
                log.error("롤백 중 오류 발생: " + rollbackEx.getMessage());
            }
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "유저 저장 중 오류 발생");
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

    /**
     * 로그인 처리를 수행합니다
     *
     * 이전 세션을 무효화하고 새로운 세션을 생성한 후, 유저 정보를 세션에 저장합니다
     * 이메일과 비밀번호를 검증한 후 유저 정보를 반환합니다
     *
     * @param loginReqDto 로그인 요청 정보를 담은 DTO
     * @param request HttpServletRequest 객체로 세션을 관리
     * @return 로그인된 유저 정보를 포함한 응답 DTO
     */
    public LoginRespDto login(LoginReqDto loginReqDto, HttpServletRequest request){

        HttpSession session = request.getSession(false);

        //이전 세션 무효화
        if (session != null)
            session.invalidate();

        session = request.getSession(true);

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            //유저 확인
            User userPS = userRepository.findByEmail(connection, loginReqDto.getEmail()).orElseThrow(
                    () -> {
                        log.warn("로그인 실패: 존재하지 않는 이메일");
                        throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "이메일이 일치하지 않습니다");
                    }
            );

            //비밀번호 검증
            if (!CustomPasswordUtil.matches(loginReqDto.getPassword(), userPS.getPassword())) {
                log.warn("로그인 실패: 비밀번호 불일치");
                throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "비밀번호가 일치하지 않습니다");
            }

            //세션에 유저 담기
            session.setAttribute("userId", userPS.getId());
            log.info("로그인 성공: 유저 ID {}", session.getAttribute("userId"));

            //아이디 반환
            return new LoginRespDto(userPS);
        }catch (SQLException e) {
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








}
