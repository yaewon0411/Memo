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

/**
 * 일정 관리 작업을 처리하는 클래스입니다
 *
 * 이 클래스는 일정을 생성, 수정, 삭제, 조회하는 HTTP 요청을 처리합니다
 * ScheduleService와 상호작용하여 필요한 작업을 수행하며,
 * 응답은 일관된 API 응답 형식을 위해 ApiUtil을 사용해 래핑됩니다
 *
 * 대부분의 작업은 세션이 유효해야만 접근이 가능합니다
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Validated
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 일정 ID로 일정을 삭제합니다
     *
     * @param scheduleId 삭제할 일정의 ID
     * @param session 현재 사용자의 세션
     * @return 삭제 작업의 성공 여부를 나타내는 응답
     */
    @DeleteMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable(name = "scheduleId")Long scheduleId, HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.deleteSchedule(scheduleId, session)), HttpStatus.OK);
    }

    /**
     * 일정의 내용, 시작 시간, 종료 시간을 수정합니다
     *
     * @param scheduleId 수정할 일정의 ID
     * @param scheduleModifyReqDto 수정할 일정 정보를 담고 있는 DTO
     * @param bindingResult 유효성 검사 오류를 포함하는 객체
     * @param session 현재 사용자의 세션
     * @return 수정된 일정 정보
     */
    @PatchMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<?>updateSchedule(@PathVariable(name = "scheduleId")Long scheduleId,
                                           @RequestBody @Validated ScheduleModifyReqDto scheduleModifyReqDto,
                                           BindingResult bindingResult,
                                           HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.updateSchedule(scheduleModifyReqDto, scheduleId, session)), HttpStatus.OK);
    }


    /**
     * 공개된 일정 목록을 수정일 또는 작성자명(또는 둘 다)으로 필터링하여 조회합니다
     *
     * @param page 페이지 번호 (기본값: 0)
     * @param limit 한 페이지당 최대 일정 수 (기본값: 10)
     * @param modifiedAt 수정일 필터
     * @param startModifiedAt 수정일 범위의 시작 날짜
     * @param endModifiedAt 수정일 범위의 종료 날짜
     * @param authorName 작성자 이름으로 필터링
     * @return 필터링된 공개 일정 목록을 포함하는 응답
     */
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

    /**
     * 현재 사용자의 일정 목록을 수정일로 필터링하여 조회합니다
     *
     * @param page 페이지 번호 (기본값: 0)
     * @param limit 한 페이지당 최대 일정 수 (기본값: 10)
     * @param modifiedAt 수정일 필터
     * @param startModifiedAt 수정일 범위의 시작 날짜
     * @param endModifiedAt 수정일 범위의 종료 날짜
     * @param session 현재 사용자의 세션
     * @return 필터링된 사용자 일정을 포함하는 응답
     */
    @GetMapping("/s/schedules")
    public ResponseEntity<?> findUserSchedules(@RequestParam(name = "page", defaultValue = "0") @Min(0) Long page,
                                          @RequestParam(name = "limit", defaultValue = "10") @Min(1) Long limit,
                                          @RequestParam(name = "modifiedAt", required = false) @Pattern(regexp = "^(30m|1h|1d|1w|1m|3m|6m)$", message = "유효하지 않은 modifiedAt 값입니다") String modifiedAt,
                                          @RequestParam(name = "startModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String startModifiedAt,
                                          @RequestParam(name = "endModifiedAt", required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다") String endModifiedAt,
                                          HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.findUserSchedules(session, page, limit, modifiedAt, startModifiedAt, endModifiedAt)), HttpStatus.OK);
    }

    /**
     * 일정 ID로 특정 일정을 조회합니다
     * 세션에 유저 정보가 없을 경우, 일정의 공개 여부를 확인하여 반환합니다
     *
     * @param scheduleId 조회할 일정의 ID
     * @param session 현재 사용자의 세션
     * @return 조회된 일정의 세부 정보
     */
    @GetMapping("/s/schedules/{scheduleId}")
    public ResponseEntity<?> findUserScheduleById(@PathVariable(name = "scheduleId")Long scheduleId,  HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.findUserScheduleById(scheduleId, session)),HttpStatus.OK);
    }

    /**
     * 새로운 일정을 생성합니다
     *
     * @param scheduleCreateReqDto 생성할 일정 정보를 담고 있는 DTO
     * @param bindingResult 유효성 검사 오류를 포함하는 객체
     * @param session 현재 사용자의 세션
     * @return 생성된 일정 정보
     */
    @PostMapping("/s/schedules")
    public ResponseEntity<?> createSchedule(@RequestBody @Valid ScheduleCreateReqDto scheduleCreateReqDto, BindingResult bindingResult,
                                            HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(scheduleService.createSchedule(scheduleCreateReqDto, session)), HttpStatus.CREATED);
    }
}
