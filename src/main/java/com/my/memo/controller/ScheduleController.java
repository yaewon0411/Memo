package com.my.memo.controller;

import com.my.memo.service.ScheduleService;
import com.my.memo.util.api.ApiUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.schedule.ReqDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    //공개 일정 단건 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getPublicScheduleById(@PathVariable(name = "scheduleId")Long scheduleId){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.findScheduleById(scheduleId)), HttpStatus.OK);
    }

    //일정 삭제
    @DeleteMapping("/s/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable(name = "scheduleId")Long scheduleId, HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.deleteSchedule(scheduleId, session)), HttpStatus.OK);
    }

    //일정 수정 (내용, 시작 시간, 종료 시간)
    @PatchMapping("/s/{scheduleId}")
    public ResponseEntity<?>modifySchedule(@PathVariable(name = "scheduleId")Long scheduleId,
                                           @RequestBody @Validated ScheduleModifyReqDto scheduleModifyReqDto,
                                           HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.modifySchedule(scheduleModifyReqDto, scheduleId, session)), HttpStatus.OK);
    }


    //공개 일정 다건 조회 (필터링: 수정일, 작성자명, 또는 수정일&작성자명 동시에)
    @GetMapping()
    public ResponseEntity<?> getAll(@RequestParam(name = "page", defaultValue = "0")Long page,
                                       @RequestParam(name = "limit", defaultValue = "10")Long limit,
                                       @RequestParam(name = "modifiedAt", required = false) String modifiedAt,
                                       @RequestParam(name = "authorName", required = false) String authorName){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.findAll(page, limit, modifiedAt, authorName)), HttpStatus.OK);
    }

    //유저 일정 다건 조회
    @GetMapping("/s")
    public ResponseEntity<?> getAllByUser(@RequestParam(name = "page", defaultValue = "0")Long page,
                                       @RequestParam(name = "limit", defaultValue = "10")Long limit, HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.findAllByUser(session, page, limit)), HttpStatus.OK);
    }

    //유저 일정 단건 조회
    @GetMapping("/s/{scheduleId}")
    public ResponseEntity<?> getUserScheduleById(@PathVariable(name = "scheduleId")Long scheduleId,  HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.findUserScheduleById(scheduleId, session)),HttpStatus.OK);
    }

    //일정 생성
    @PostMapping("/s")
    public ResponseEntity<?> createSchedule(@RequestBody @Valid ScheduleCreateReqDto scheduleCreateReqDto, BindingResult bindingResult,
                                            HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.createSchedule(scheduleCreateReqDto, session)), HttpStatus.CREATED);
    }
}
