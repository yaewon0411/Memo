package com.my.memo.controller;

import com.my.memo.service.ScheduleService;
import com.my.memo.util.api.ApiUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.schedule.ReqDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Validated
public class ScheduleController {

    private final ScheduleService scheduleService;

    //일정 삭제
    @DeleteMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable(name = "scheduleId")Long scheduleId, HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.deleteSchedule(scheduleId, session)), HttpStatus.OK);
    }

    //일정 수정 (내용, 시작 시간, 종료 시간)
    @PatchMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<?>updateSchedule(@PathVariable(name = "scheduleId")Long scheduleId,
                                           @RequestBody @Validated ScheduleModifyReqDto scheduleModifyReqDto,
                                           BindingResult bindingResult,
                                           HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.updateSchedule(scheduleModifyReqDto, scheduleId, session)), HttpStatus.OK);
    }


    //공개 일정 다건 조회 (필터링: 수정일, 작성자명, 또는 수정일&작성자명 동시에)
    @GetMapping("/schedules")
    public ResponseEntity<?> findPublicSchedules(
            @RequestParam(name = "page", defaultValue = "0") @Min(0) Long page,
            @RequestParam(name = "limit", defaultValue = "10") @Min(1) Long limit,
            @RequestParam(name = "modifiedAt", required = false) @Pattern(regexp = "^(30m|1h|1d|1w|1m|3m|6m)$", message = "유효하지 않은 modifiedAt 값입니다") String modifiedAt,
            @RequestParam(name = "startModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String startModifiedAt,
            @RequestParam(name = "endModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String endModifiedAt,
            @RequestParam(name = "authorName", required = false) String authorName) {

        return new ResponseEntity<>(ApiUtil.success(scheduleService.findPublicSchedulesWithFilters(page, limit, modifiedAt, authorName, startModifiedAt, endModifiedAt)), HttpStatus.OK);
    }

    //유저 일정 다건 조회
    @GetMapping("/s/schedules")
    public ResponseEntity<?> findUserSchedules(@RequestParam(name = "page", defaultValue = "0") @Min(0) Long page,
                                          @RequestParam(name = "limit", defaultValue = "10") @Min(1) Long limit,
                                          @RequestParam(name = "modifiedAt", required = false) @Pattern(regexp = "^(30m|1h|1d|1w|1m|3m|6m)$", message = "유효하지 않은 modifiedAt 값입니다") String modifiedAt,
                                          @RequestParam(name = "startModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String startModifiedAt,
                                          @RequestParam(name = "endModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String endModifiedAt,
                                          HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.findUserSchedules(session, page, limit, modifiedAt, startModifiedAt, endModifiedAt)), HttpStatus.OK);
    }

    //일정 단건 조회
    @GetMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<?> findUserScheduleById(@PathVariable(name = "scheduleId")Long scheduleId,  HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.findUserScheduleById(scheduleId, session)),HttpStatus.OK);
    }

    //일정 생성
    @PostMapping("/s/schedules")
    public ResponseEntity<?> createSchedule(@RequestBody @Valid ScheduleCreateReqDto scheduleCreateReqDto, BindingResult bindingResult,
                                            HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.createSchedule(scheduleCreateReqDto, session)), HttpStatus.CREATED);
    }
}
