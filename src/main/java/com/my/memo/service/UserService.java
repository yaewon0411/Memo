package com.my.memo.service;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import com.my.memo.util.CustomPasswordUtil;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static com.my.memo.dto.user.ReqDto.*;
import static com.my.memo.dto.user.RespDto.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    //로그아웃
    public UserLogoutRespDto logout(HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        session.invalidate();
        log.info("로그아웃 완료: 유저 ID {}", userId);
        return new UserLogoutRespDto(true, userId);
    }


    //유저 삭제
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



    //유저 정보 조회
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


    //유저 정보 수정
    public UserModifyRespDto modifyUserInfo(UserModifyReqDto userModifyReqDto, HttpSession session){

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


    //회원가입
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

    //로그인
    public LoginRespDto login(LoginReqDto loginReqDto, HttpSession session){

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
