package com.my.memo.service;

import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepositoryImpl;
import com.my.memo.dto.user.ReqDto;
import com.my.memo.dto.user.RespDto;
import com.my.memo.ex.CustomApiException;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.jaas.LoginExceptionResolver;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.my.memo.dto.user.ReqDto.*;
import static com.my.memo.dto.user.RespDto.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepositoryImpl userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    //회원가입
    @Transactional
    public JoinRespDto join(JoinReqDto joinReqDto){

        //이메일 중복 검사
        if(userRepository.existsByEmail(joinReqDto.getEmail())) {
            log.warn("이메일 중복: {}", joinReqDto.getEmail());
            throw new CustomApiException(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다");
        }

        //저장
        User user = joinReqDto.toEntity(bCryptPasswordEncoder);
        Long userId = userRepository.save(user);
        log.info("회원가입 완료: id = {}", userId);

        //반환
        return new JoinRespDto(userId, user);
    }

    //로그인
    public LoginRespDto login(LoginReqDto loginReqDto){


        return null;
    }

    public static class LoginRespDto{

    }
    public static class LoginReqDto{

    }





}
