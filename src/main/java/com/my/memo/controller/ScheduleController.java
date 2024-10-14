package com.my.memo.controller;

import com.my.memo.config.jwt.RequireAuth;
import com.my.memo.config.user.UserId;
import com.my.memo.domain.user.Role;
import com.my.memo.service.CommentService;
import com.my.memo.service.ScheduleService;
import com.my.memo.service.ScheduleUserService;
import com.my.memo.util.api.ApiResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
import static com.my.memo.dto.scheduleUser.ReqDto.AssignedUserDeleteReqDto;
import static com.my.memo.dto.scheduleUser.ReqDto.UserAssignReqDto;
import static com.my.memo.dto.scheduleUser.RespDto.AssignedUserDeleteRespDto;
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
    @DeleteMapping("/schedules/{scheduleId}/users")
    public ResponseEntity<ApiResult<AssignedUserDeleteRespDto>> deleteAssignedUser(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                                   @RequestBody @Valid AssignedUserDeleteReqDto assignedUserDeleteReqDto,
                                                                                   @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(scheduleUserService.deleteAssignedUser(scheduleId, assignedUserDeleteReqDto, userId)), HttpStatus.OK);
    }

    @RequireAuth(role = Role.USER)
    @DeleteMapping("/schedules/{scheduleId}/comments/{commentId}")
    public ResponseEntity<ApiResult<CommentDeleteRespDto>> deleteComment(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                         @PathVariable(name = "commentId") Long commentId,
                                                                         @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(commentService.deleteComment(scheduleId, commentId, userId)), HttpStatus.OK);
    }

    @RequireAuth(role = Role.USER)
    @PutMapping("/schedules/{scheduleId}/comments/{commentId}")
    public ResponseEntity<ApiResult<CommentModifyRespDto>> updateComment(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                         @PathVariable(name = "commentId") Long commentId,
                                                                         @RequestBody @Valid CommentModifyReqDto commentModifyReqDto,
                                                                         @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(commentService.updateComment(scheduleId, commentId, commentModifyReqDto, userId)), HttpStatus.OK);
    }

    @RequireAuth(role = Role.USER)
    @PostMapping("/schedules/{scheduleId}/comments")
    public ResponseEntity<ApiResult<CommentCreateRespDto>> createComment(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                         @RequestBody @Valid CommentCreateReqDto commentCreateReqDto,
                                                                         @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(commentService.createComment(scheduleId, commentCreateReqDto, userId)), HttpStatus.CREATED);
    }


    //유저 스케줄에 할당
    @RequireAuth(role = Role.USER)
    @PostMapping("/schedules/{scheduleId}/users")
    public ResponseEntity<ApiResult<UserAssignRespDto>> assignUserToSchedule(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                             @RequestBody @Valid UserAssignReqDto userAssignReqDto,
                                                                             @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(scheduleUserService.assignUserToSchedule(scheduleId, userAssignReqDto, userId)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.ADMIN)
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResult<ScheduleDeleteRespDto>> deleteSchedule(@PathVariable(name = "scheduleId") Long scheduleId, @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.deleteSchedule(scheduleId, userId)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.ADMIN)
    @PatchMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResult<ScheduleModifyRespDto>> updateSchedule(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                           @RequestBody @Validated ScheduleModifyReqDto scheduleModifyReqDto,
                                                                           @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.updateSchedule(scheduleModifyReqDto, scheduleId, userId)), HttpStatus.OK);
    }


    @GetMapping("/public-schedules")
    public ResponseEntity<ApiResult<PublicScheduleListRespDto>> findPublicSchedules(@Valid PublicScheduleFilter publicScheduleFilter) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findPublicSchedulesWithFilters(publicScheduleFilter)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @GetMapping("/schedules/users")
    public ResponseEntity<ApiResult<UserScheduleListRespDto>> findUserSchedules(@Valid UserScheduleFilter requestScheduleFilter,
                                                                                @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findUserSchedules(requestScheduleFilter, userId)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @GetMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResult<ScheduleRespDto>> findScheduleById(@PathVariable(name = "scheduleId") Long scheduleId,
                                                                       @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
                                                                       @RequestParam(name = "limit", defaultValue = "10") @Min(1) int limit,
                                                                       @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findScheduleById(scheduleId, page, limit, userId)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @PostMapping("/schedules")
    public ResponseEntity<ApiResult<ScheduleCreateRespDto>> createSchedule(@RequestBody @Valid ScheduleCreateReqDto scheduleCreateReqDto,
                                                                           @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.createSchedule(scheduleCreateReqDto, userId)), HttpStatus.CREATED);
    }
}
