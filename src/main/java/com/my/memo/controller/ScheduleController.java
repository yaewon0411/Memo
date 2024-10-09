package com.my.memo.controller;

import com.my.memo.config.jwt.RequireAuth;
import com.my.memo.domain.user.Role;
import com.my.memo.service.CommentService;
import com.my.memo.service.ScheduleService;
import com.my.memo.service.ScheduleUserService;
import com.my.memo.util.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.comment.ReqDto.CommentCreateReqDto;
import static com.my.memo.dto.schedule.ReqDto.ScheduleCreateReqDto;
import static com.my.memo.dto.schedule.ReqDto.ScheduleModifyReqDto;


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
    public ResponseEntity<?> findPublicSchedules(
            @RequestParam(name = "page", defaultValue = "0", required = false) @Min(0) Long page,
            @RequestParam(name = "limit", defaultValue = "10", required = false) @Min(1) Long limit,
            @RequestParam(name = "modifiedAt", required = false) @Pattern(regexp = "^(30m|1h|1d|1w|1m|3m|6m)$", message = "유효하지 않은 modifiedAt 값입니다") String modifiedAt,
            @RequestParam(name = "startModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String startModifiedAt,
            @RequestParam(name = "endModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String endModifiedAt,
            @RequestParam(name = "authorName", required = false) String authorName) {

        return new ResponseEntity<>(ApiResult.success(scheduleService.findPublicSchedulesWithFilters(page, limit, modifiedAt, authorName, startModifiedAt, endModifiedAt)), HttpStatus.OK);
    }


    @RequireAuth
    @GetMapping("/s/schedules")
    public ResponseEntity<?> findUserSchedules(@RequestParam(name = "page", defaultValue = "0") @Min(0) Long page,
                                               @RequestParam(name = "limit", defaultValue = "10") @Min(1) Long limit,
                                               @RequestParam(name = "modifiedAt", required = false) @Pattern(regexp = "^(30m|1h|1d|1w|1m|3m|6m)$", message = "유효하지 않은 modifiedAt 값입니다") String modifiedAt,
                                               @RequestParam(name = "startModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String startModifiedAt,
                                               @RequestParam(name = "endModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String endModifiedAt,
                                               HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(scheduleService.findUserSchedules(request, page, limit, modifiedAt, startModifiedAt, endModifiedAt)), HttpStatus.OK);
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
