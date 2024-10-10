package com.my.memo.controller;

import com.my.memo.config.jwt.RequireAuth;
import com.my.memo.domain.user.Role;
import com.my.memo.service.CommentService;
import com.my.memo.service.ScheduleService;
import com.my.memo.service.ScheduleUserService;
import com.my.memo.util.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.comment.ReqDto.CommentCreateReqDto;
import static com.my.memo.dto.schedule.ReqDto.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Validated
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleUserService scheduleUserService;
    private final CommentService commentService;

    @RequireAuth
    @PostMapping("/s/schedules/{scheduleId}/comments")
    public ResponseEntity<?> createComment(@PathVariable(name = "scheduleId") Long scheduleId,
                                           @RequestBody @Valid CommentCreateReqDto commentCreateReqDto,
                                           HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(commentService.createComment(scheduleId, commentCreateReqDto, request)), HttpStatus.CREATED);
    }


    //유저 스케줄에 할당
    @RequireAuth
    @PostMapping("/s/schedules/{scheduleId}/users/{userId}")
    public ResponseEntity<?> assignUserToSchedule(@PathVariable(name = "scheduleId") Long scheduleId,
                                                  @PathVariable(name = "userId") Long userId,
                                                  HttpServletRequest request) {

        return new ResponseEntity<>(ApiResult.success(scheduleUserService.assignUserToSchedule(scheduleId, userId, request)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.ADMIN)
    @DeleteMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable(name = "scheduleId") Long scheduleId, HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.deleteSchedule(scheduleId, request)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.ADMIN)
    @PatchMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<?> updateSchedule(@PathVariable(name = "scheduleId") Long scheduleId,
                                            @RequestBody @Validated ScheduleModifyReqDto scheduleModifyReqDto,
                                            HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.updateSchedule(scheduleModifyReqDto, scheduleId, request)), HttpStatus.OK);
    }


    @GetMapping("/schedules")
    public ResponseEntity<?> findPublicSchedules(@Valid PublicScheduleFilter publicScheduleFilter) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findPublicSchedulesWithFilters(publicScheduleFilter)), HttpStatus.OK);
    }


    @RequireAuth
    @GetMapping("/s/schedules")
    public ResponseEntity<?> findUserSchedules(@Valid UserScheduleFilter userScheduleFilter,
                                               HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findUserSchedules(userScheduleFilter, request)), HttpStatus.OK);
    }


    //
    /*
     * TODO 토큰 없으면 -> 공개 스케줄만 단 건 반환 가능
     * TODO 토큰 있으면 -> 공개 스케줄이던 본인 스케줄이던 단 건 반환 가능해야 함
     *
     * */
    @RequireAuth
    @GetMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<?> findUserScheduleById(@PathVariable(name = "scheduleId") Long scheduleId, HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findUserScheduleById(scheduleId, request)), HttpStatus.OK);
    }


    @RequireAuth
    @PostMapping("/s/schedules")
    public ResponseEntity<?> createSchedule(@RequestBody @Valid ScheduleCreateReqDto scheduleCreateReqDto,
                                            HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.createSchedule(scheduleCreateReqDto, request)), HttpStatus.CREATED);
    }
}
