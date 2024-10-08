package com.my.memo.service;

import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.comment.CommentRepository;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.schedule.ScheduleRepository;
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
import static com.my.memo.dto.comment.RespDto.CommentCreateRespDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //댓글 저장
    @Transactional
    public CommentCreateRespDto createComment(Long scheduleId, CommentCreateReqDto commentReqDto, HttpServletRequest request) {
        //유저 정보 꺼내기
        Long userId = (Long) request.getAttribute("userId");
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다")
        );

        //일정 찾기
        Schedule schedulePS = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomApiException(HttpStatus.NOT_FOUND.value(), "해당 일정은 존재하지 않습니다")
        );

        //코멘트 저장
        Comment commentPS = commentRepository.save(
                Comment.builder()
                        .content(commentReqDto.getContent())
                        .user(userPS)
                        .schedule(schedulePS)
                        .build()
        );

        return new CommentCreateRespDto(commentPS);
    }


    //댓글 삭제
}
