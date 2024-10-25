package com.my.memo.util.entity;

import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.comment.CommentRepository;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
import com.my.memo.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityValidator {

    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Schedule validateAndGetSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomApiException(ErrorCode.SCHEDULE_NOT_FOUND)
        );
    }

    public Comment validateAndGetComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CustomApiException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }

    public User validateAndGetUser(Long userId) {

        if (userId == null) {
            throw new CustomApiException(HttpStatus.UNAUTHORIZED.value(), "재로그인이 필요합니다");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 유저 접근 시도: ID {}", userId);
                    return new CustomApiException(ErrorCode.USER_NOT_FOUND);
                });
    }

}
