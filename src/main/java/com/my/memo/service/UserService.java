package com.my.memo.service;

import com.my.memo.config.jwt.JwtProvider;
import com.my.memo.domain.comment.CommentRepository;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.scheduleUser.ScheduleUserRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import com.my.memo.util.CustomPasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.my.memo.dto.user.ReqDto.*;
import static com.my.memo.dto.user.RespDto.*;

/**
 * 유저 관련 서비스 클래스입니다
 * <p>
 * 로그인, 로그아웃, 회원가입, 정보 조회, 정보 수정, 유저 삭제와 관련된 비즈니스 로직을 처리합니다
 * 트랜잭션 관리와 예외 처리를 직접 처리합니다
 */
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


    //TODO 다시 확인
    @Transactional
    public UserDeleteRespDto deleteUser(HttpServletRequest request) {

        User userPS = validateAndGetuser(request);
        userRepository.findUserWithSchedulesById(userPS.getId());

        int deletedAssignedCnt = scheduleUserRepository.deleteByUser(userPS, userPS.getScheduleList());
        log.info("해당 유저 ID {} 배정 기록 삭제: 삭제된 개수 {}", userPS.getId(), deletedAssignedCnt);

        int deletedCommentCnt = commentRepository.deleteByUser(userPS);
        log.info("해당 유저 ID {} 코멘트 삭제: 삭제된 개수 {}", userPS.getId(), deletedCommentCnt);

        int deletedScheduleCnt = scheduleRepository.deleteByUser(userPS);
        log.info("해당 유저 ID {} 일정 삭제: 삭제된 개수 {}", userPS.getId(), deletedScheduleCnt);

        userRepository.delete(userPS);
        return new UserDeleteRespDto(true, userPS.getId());
    }

    public UserRespDto getUserInfo(HttpServletRequest request) {
        User userPS = validateAndGetuser(request);
        return new UserRespDto(userPS);
    }


    @Transactional
    public UserModifyRespDto updateUser(UserModifyReqDto userModifyReqDto, HttpServletRequest request) {

        User userPS = validateAndGetuser(request);

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


    public LoginRespDto login(LoginReqDto loginReqDto, HttpServletResponse response) {

        User userPS = userRepository.findUserByEmail(loginReqDto.getEmail()).orElseThrow(
                () -> new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "이메일이 일치하지 않습니다")
        );

        // 비밀번호 검증
        if (!CustomPasswordUtil.matches(loginReqDto.getPassword(), userPS.getPassword())) {
            log.warn("로그인 실패: 비밀번호 불일치");
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "비밀번호가 일치하지 않습니다");
        }

        String jwt = jwtProvider.create(userPS);

        jwtProvider.addJwtToHeader(jwt, response);

        log.info("로그인 성공: 유저 ID {}", userPS.getId());

        return new LoginRespDto(userPS);
    }

    private User validateAndGetuser(HttpServletRequest request) {
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


}
