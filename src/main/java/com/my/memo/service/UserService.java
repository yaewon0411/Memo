package com.my.memo.service;

import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import com.my.memo.util.CustomPasswordUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static com.my.memo.dto.user.ReqDto.*;
import static com.my.memo.dto.user.RespDto.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

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
            } catch (SQLException rollbackEx) {
                log.error("롤백 중 오류 발생: " + rollbackEx.getMessage());
            }
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "유저 저장 중 오류 발생");
        }catch (CustomApiException e) { //비즈니스 에러
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                log.error("롤백 중 오류 발생: " + rollbackEx.getMessage());
            }
            throw e;
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
