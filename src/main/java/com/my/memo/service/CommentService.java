package com.my.memo.service;

import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.comment.CommentRepository;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.user.Role;
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

import static com.my.memo.dto.comment.ReqDto.CommentCreateReqDto;
import static com.my.memo.dto.comment.ReqDto.CommentModifyReqDto;
import static com.my.memo.dto.comment.RespDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //댓글 저장
    @Transactional
    public CommentCreateRespDto createComment(Long scheduleId, CommentCreateReqDto commentReqDto, HttpServletRequest request) {

        User userPS = validateAndGetUser(request);

        //일정 찾기
        Schedule schedulePS = validateAndGetSchedule(scheduleId);

        //코멘트 저장
        Comment commentPS = commentRepository.save(commentReqDto.toEntity(userPS, schedulePS));

        return new CommentCreateRespDto(commentPS);
    }

    // 댓글 수정
    @Transactional
    public CommentModifyRespDto updateComment(Long scheduleId, Long commentId, CommentModifyReqDto commentModifyReqDto, HttpServletRequest request) {

        User userPS = validateAndGetUser(request);

        validateAndGetSchedule(scheduleId);

        Comment commentPS = validateAndGetComment(commentId);

        //관리자가 아니라면 댓글 작성자 본인이여야 함
        if (!userPS.getRole().equals(Role.ADMIN) && !commentPS.getUser().equals(userPS)) {
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "해당 댓글에 접근할 권한이 없습니다");
        }

        commentPS.modify(commentModifyReqDto);

        commentRepository.saveAndFlush(commentPS);
        log.info("코멘트 수정 완료: 코멘트 ID {}", commentPS.getId());

        return new CommentModifyRespDto(commentPS);
    }

    //댓글 삭제
    @Transactional
    public CommentDeleteRespDto deleteComment(Long scheduleId, Long commentId, HttpServletRequest request) {

        User userPS = validateAndGetUser(request);

        validateAndGetSchedule(scheduleId);

        Comment commentPS = validateAndGetComment(commentId);

        //관리자가 아니라면 댓글 작성자 본인이여야 함
        if (!userPS.getRole().equals(Role.ADMIN) && !commentPS.getUser().equals(userPS)) {
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "해당 댓글에 접근할 권한이 없습니다");
        }

        commentRepository.delete(commentPS);
        log.info("코멘트 삭제 완료: 코멘트 ID {}", commentId);

        return new CommentDeleteRespDto(commentId, true);
    }

    private Schedule validateAndGetSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
        );
    }

    private Comment validateAndGetComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 댓글은 존재하지 않습니다")
        );
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


}
