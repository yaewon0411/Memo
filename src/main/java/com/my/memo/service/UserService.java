package com.my.memo.service;

import com.my.memo.config.auth.jwt.JwtProvider;
import com.my.memo.domain.comment.CommentRepository;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.scheduleUser.ScheduleUserRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import com.my.memo.util.CustomPasswordUtil;
import com.my.memo.util.entity.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.my.memo.dto.user.ReqDto.*;
import static com.my.memo.dto.user.RespDto.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ScheduleUserRepository scheduleUserRepository;
    private final ScheduleRepository scheduleRepository;
    private final JwtProvider jwtProvider;
    private final EntityValidator entityValidator;


    @Transactional
    public UserDeleteRespDto deleteUser(Long userId) {

        User userPS = userRepository.findUserWithSchedulesById(userId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
        );

        int deletedAssignedCnt = scheduleUserRepository.deleteByUserId(userId, userPS.getScheduleList());
        log.info("유저 ID의 {} 배정 기록 삭제: 삭제된 개수 {}", userPS.getId(), deletedAssignedCnt);

        int deletedCommentCnt = commentRepository.deleteByUserId(userId);
        log.info("유저 ID {} 가 작성한 코멘트 삭제: 삭제된 개수 {}", userPS.getId(), deletedCommentCnt);

        int deletedScheduleCommentCnt = commentRepository.deleteBySchedules(userPS.getScheduleList());
        log.info("유저 ID {} 가 생성한 일정에 달린 모든 코멘트 삭제: 삭제된 개수 {}", userId, deletedScheduleCommentCnt);

        int deletedScheduleCnt = scheduleRepository.deleteByUserId(userId);
        log.info("해당 유저 ID {} 일정 삭제: 삭제된 개수 {}", userPS.getId(), deletedScheduleCnt);

        userRepository.deleteById(userId);
        return new UserDeleteRespDto(true, userId);
    }

    public UserRespDto getUserInfo(Long userId) {
        User userPS = entityValidator.validateAndGetUser(userId);
        return new UserRespDto(userPS);
    }


    @Transactional
    public UserModifyRespDto updateUser(UserModifyReqDto userModifyReqDto, Long userId) {

        User userPS = entityValidator.validateAndGetUser(userId);

        //수정 요청한 이메일이 사용중인 이메일인지 검사
        if (userModifyReqDto.getEmail() != null && !userPS.getEmail().equals(userModifyReqDto.getEmail())) {
            if (userRepository.existsUserByEmail(userModifyReqDto.getEmail()))
                throw new CustomApiException(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다");
        }

        userPS.modify(userModifyReqDto);

        return new UserModifyRespDto(userPS);
    }


    @Transactional
    public JoinRespDto join(JoinReqDto joinReqDto) {

        //이메일 중복 검사
        if (userRepository.existsUserByEmail(joinReqDto.getEmail())) {
            log.warn("이메일 중복: {}", joinReqDto.getEmail());
            throw new CustomApiException(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다");
        }

        User userPS = userRepository.save(joinReqDto.toEntity());
        log.info("회원가입 완료: 유저 ID {}", userPS.getId());

        return new JoinRespDto(userPS);
    }


    public LoginRespDto login(LoginReqDto loginReqDto) {

        User userPS = userRepository.findUserByEmail(loginReqDto.getEmail()).orElseThrow(
                () -> new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "이메일이 일치하지 않습니다")
        );

        // 비밀번호 검증
        if (!CustomPasswordUtil.matches(loginReqDto.getPassword(), userPS.getPassword())) {
            log.warn("로그인 실패: 비밀번호 불일치");
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "비밀번호가 일치하지 않습니다");
        }

        String jwt = jwtProvider.create(userPS);

        return new LoginRespDto(userPS, jwt);
    }


}
