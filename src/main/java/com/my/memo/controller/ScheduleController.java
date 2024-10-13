package com.my.memo.controller;

import com.my.memo.config.jwt.RequireAuth;
import com.my.memo.domain.user.Role;
import com.my.memo.domain.user.User;
import com.my.memo.service.CommentService;
import com.my.memo.service.ScheduleService;
import com.my.memo.service.ScheduleUserService;
import com.my.memo.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.comment.ReqDto.CommentCreateReqDto;
import static com.my.memo.dto.comment.ReqDto.CommentModifyReqDto;
import static com.my.memo.dto.comment.RespDto.*;
import static com.my.memo.dto.schedule.ReqDto.*;
import static com.my.memo.dto.schedule.RespDto.*;
import static com.my.memo.dto.scheduleUser.ReqDto.UserAssignReqDto;
import static com.my.memo.dto.scheduleUser.RespDto.UserAssignRespDto;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Validated
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleUserService scheduleUserService;
    private final CommentService commentService;

    @RequireAuth(role = Role.USER)
    @DeleteMapping("/s/schedules/{scheduleId}/comments/{commentId}")
    public ResponseEntity<ApiResult<CommentDeleteRespDto>> deleteComment(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                         @PathVariable(name = "commentId") Long commentId,
                                                                         User user) {
        return new ResponseEntity<>(ApiResult.success(commentService.deleteComment(scheduleId, commentId, user)), HttpStatus.OK);
    }

    @RequireAuth(role = Role.USER)
    @PutMapping("/s/schedules/{scheduleId}/comments/{commentId}")
    public ResponseEntity<ApiResult<CommentModifyRespDto>> updateComment(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                         @PathVariable(name = "commentId") Long commentId,
                                                                         @RequestBody @Valid CommentModifyReqDto commentModifyReqDto,
                                                                         User user) {
        return new ResponseEntity<>(ApiResult.success(commentService.updateComment(scheduleId, commentId, commentModifyReqDto, user)), HttpStatus.OK);
    }

    @RequireAuth(role = Role.USER)
    @PostMapping("/s/schedules/{scheduleId}/comments")
    public ResponseEntity<ApiResult<CommentCreateRespDto>> createComment(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                         @RequestBody @Valid CommentCreateReqDto commentCreateReqDto,
                                                                         User user) {
        return new ResponseEntity<>(ApiResult.success(commentService.createComment(scheduleId, commentCreateReqDto, user)), HttpStatus.CREATED);
    }


    //유저 스케줄에 할당
    @RequireAuth(role = Role.USER)
    @PostMapping("/s/schedules/{scheduleId}/users/{userId}")
    public ResponseEntity<ApiResult<UserAssignRespDto>> assignUserToSchedule(@RequestBody @Valid UserAssignReqDto userAssignReqDto,
                                                                             User user) {
        return new ResponseEntity<>(ApiResult.success(scheduleUserService.assignUserToSchedule(userAssignReqDto, user)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.ADMIN)
    @DeleteMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<ApiResult<ScheduleDeleteRespDto>> deleteSchedule(@PathVariable(name = "scheduleId") Long scheduleId, User user) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.deleteSchedule(scheduleId, user)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.ADMIN)
    @PatchMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<ApiResult<ScheduleModifyRespDto>> updateSchedule(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                           @RequestBody @Validated ScheduleModifyReqDto scheduleModifyReqDto,
                                                                           User user) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.updateSchedule(scheduleModifyReqDto, scheduleId, user)), HttpStatus.OK);
    }


    @GetMapping("/schedules")
    public ResponseEntity<ApiResult<PublicScheduleListRespDto>> findPublicSchedules(@Valid PublicScheduleFilter publicScheduleFilter) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findPublicSchedulesWithFilters(publicScheduleFilter)), HttpStatus.OK);
    }


    @RequireAuth
    @GetMapping("/s/schedules")
    public ResponseEntity<ApiResult<UserScheduleListRespDto>> findUserSchedules(@Valid UserScheduleFilter userScheduleFilter,
                                                                                User user) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findUserSchedules(userScheduleFilter, user)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @GetMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<ApiResult<ScheduleRespDto>> findScheduleById(@PathVariable(name = "scheduleId") Long scheduleId, User user) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findScheduleById(scheduleId, user)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @PostMapping("/s/schedules")
    public ResponseEntity<ApiResult<ScheduleCreateRespDto>> createSchedule(@RequestBody @Valid ScheduleCreateReqDto scheduleCreateReqDto,
                                                                           User user) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.createSchedule(scheduleCreateReqDto, user)), HttpStatus.CREATED);
    }
}
