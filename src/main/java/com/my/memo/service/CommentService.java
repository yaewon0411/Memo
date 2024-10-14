package com.my.memo.service;

import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.comment.CommentRepository;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.Role;
import com.my.memo.domain.user.User;
import com.my.memo.ex.CustomApiException;
import com.my.memo.util.entity.EntityValidator;
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
    private final EntityValidator entityValidator;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //댓글 저장
    @Transactional
    public CommentCreateRespDto createComment(Long scheduleId, CommentCreateReqDto commentReqDto, HttpServletRequest request) {

        User userPS = entityValidator.validateAndGetUser(request);

        //일정 찾기
        Schedule schedulePS = entityValidator.validateAndGetSchedule(scheduleId);

        //관리자도 아니고 & 공개 일정도 아니고 & 해당 일정 만든 본인도 아니면
        if (!userPS.getRole().equals(Role.ADMIN) && !schedulePS.isPublic() && !schedulePS.getUser().equals(userPS))
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "해당 일정에 접근할 수 없습니다");

        //코멘트 저장
        Comment commentPS = commentRepository.save(commentReqDto.toEntity(userPS, schedulePS));

        return new CommentCreateRespDto(commentPS);
    }

    // 댓글 수정
    @Transactional
    public CommentModifyRespDto updateComment(Long scheduleId, Long commentId, CommentModifyReqDto commentModifyReqDto, HttpServletRequest request) {

        User userPS = entityValidator.validateAndGetUser(request);

        entityValidator.validateAndGetSchedule(scheduleId);

        Comment commentPS = entityValidator.validateAndGetComment(commentId);

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

        User userPS = entityValidator.validateAndGetUser(request);

        entityValidator.validateAndGetSchedule(scheduleId);

        Comment commentPS = entityValidator.validateAndGetComment(commentId);

        //관리자가 아니라면 댓글 작성자 본인이여야 함
        if (!userPS.getRole().equals(Role.ADMIN) && !commentPS.getUser().equals(userPS)) {
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "해당 댓글에 접근할 권한이 없습니다");
        }

        commentRepository.delete(commentPS);
        log.info("코멘트 삭제 완료: 코멘트 ID {}", commentId);

        return new CommentDeleteRespDto(commentId, true);
    }


}
