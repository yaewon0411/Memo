package com.my.memo.service;

import com.my.memo.aop.valid.RequireAuthenticatedUser;
import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.comment.CommentRepository;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.user.User;
import com.my.memo.dto.comment.req.CommentCreateReqDto;
import com.my.memo.dto.comment.req.CommentModifyReqDto;
import com.my.memo.dto.comment.resp.CommentCreateRespDto;
import com.my.memo.dto.comment.resp.CommentDeleteRespDto;
import com.my.memo.dto.comment.resp.CommentModifyRespDto;
import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final ScheduleService scheduleService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //댓글 저장
    @Transactional
    @RequireAuthenticatedUser
    public CommentCreateRespDto createComment(Long scheduleId, CommentCreateReqDto commentReqDto, Long userId) {

        User userPS = userService.findByIdOrFail(userId);
        Schedule schedulePS = scheduleService.findByIdOrFail(scheduleId);

        //공개 일정이 아닌데 & 관리자도 아니면 -> 댓글 작성 불가
        if (!schedulePS.isPublic() && !userPS.isAdmin()) {
            throw new CustomApiException(ErrorCode.FORBIDDEN_SCHEDULE_ACCESS);
        }
        //코멘트 저장
        Comment commentPS = commentRepository.save(commentReqDto.toEntity(userPS, schedulePS));
        return new CommentCreateRespDto(commentPS);
    }

    // 댓글 수정
    @Transactional
    @RequireAuthenticatedUser
    public CommentModifyRespDto updateComment(Long scheduleId, Long commentId, CommentModifyReqDto commentModifyReqDto, Long userId) {

        User userPS = userService.findByIdOrFail(userId);
        Schedule schedulePS = scheduleService.findByIdOrFail(scheduleId);
        Comment commentPS = findByIdOrFail(commentId);

        //해당 스케줄에 달린 댓글이 맞는지 확인
        isCommentInSchedule(schedulePS, commentPS);

        //관리자가 아니라면 댓글 작성자 본인이여야 함
        validateCommentAccess(userPS, commentPS);

        commentPS.modify(commentModifyReqDto);
        commentRepository.saveAndFlush(commentPS);
        log.info("코멘트 수정 완료: 코멘트 ID {}", commentPS.getId());

        return new CommentModifyRespDto(commentPS);
    }

    //댓글 삭제
    @Transactional
    @RequireAuthenticatedUser
    public CommentDeleteRespDto deleteComment(Long scheduleId, Long commentId, Long userId) {

        User userPS = userService.findByIdOrFail(userId);
        Schedule schedulePS = scheduleRepository.findScheduleWithCommentsById(scheduleId)
                .orElseThrow(() -> new CustomApiException(ErrorCode.SCHEDULE_NOT_FOUND));
        Comment commentPS = findByIdOrFail(commentId);

        //해당 스케줄에 달린 댓글이 맞는지 확인
        isCommentInSchedule(schedulePS, commentPS);

        //관리자가 아니라면 댓글 작성자 본인이여야 함
        validateCommentAccess(userPS, commentPS);

        schedulePS.getCommentList().remove(commentPS);
        commentRepository.deleteById(commentId);
        log.info("코멘트 삭제 완료: 코멘트 ID {}", commentId);

        return new CommentDeleteRespDto(commentId, true);
    }

    public Comment findByIdOrFail(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CustomApiException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }

    private void isCommentInSchedule(Schedule schedule, Comment comment) {
        if (!schedule.getCommentList().contains(comment)) {
            throw new CustomApiException(ErrorCode.COMMENT_NOT_IN_SCHEDULE);
        }
    }

    private void validateCommentAccess(User user, Comment comment) {
        if (!user.isAdmin() && !comment.isAuthor(user)) {
            throw new CustomApiException(ErrorCode.FORBIDDEN_COMMENT_ACCESS);
        }
    }


}
