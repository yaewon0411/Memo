package com.my.memo.controller;

import com.my.memo.service.UserService;
import com.my.memo.util.api.ApiUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.user.ReqDto.*;
/**
 * 유저 관련 작업을 처리하는 클래스입니다
 *
 * 로그인, 로그아웃, 회원가입, 정보 조회 및 수정, 삭제 등의 HTTP 요청을 처리합니다
 * UserService와 상호작용하여 필요한 작업을 수행하며,
 * 일관된 API 응답 형식을 위해 ApiUtil을 사용해 래핑됩니다
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /**
     * 현재 사용자 로그아웃을 처리합니다
     *
     * @param session 현재 사용자의 세션
     * @return 로그아웃 성공 여부를 나타내는 응답
     */
    @PostMapping("/s/logout")
    public ResponseEntity<?>logout(HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.logout(session)), HttpStatus.OK);
    }

    /**
     * 현재 사용자의 계정을 삭제합니다
     *
     * @param session 현재 사용자의 세션
     * @return 삭제 작업의 성공 여부를 나타내는 응답
     */
    @DeleteMapping("/s/users")
    public ResponseEntity<?> deleteUser(HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.deleteUser(session)), HttpStatus.OK);
    }

    /**
     * 현재 사용자의 정보를 조회합니다
     *
     * @param session 현재 사용자의 세션
     * @return 조회된 유저 정보
     */
    @GetMapping("/s/users")
    public ResponseEntity<?> getUserInfo(HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.getUserInfo(session)), HttpStatus.OK);
    }

    /**
     * 현재 사용자의 정보를 수정합니다
     *
     * @param userModifyReqDto 수정할 사용자 정보를 담고 있는 DTO
     * @param bindingResult 유효성 검사 오류를 포함하는 객체
     * @param session 현재 사용자의 세션
     * @return 수정된 사용자 정보
     */
    @PatchMapping("/s/users")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserModifyReqDto userModifyReqDto, BindingResult bindingResult,
                                            HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.updateUser(userModifyReqDto, session)), HttpStatus.OK);
    }

    /**
     * 회원가입
     *
     * @param joinReqDto 회원가입 정보를 담고 있는 DTO
     * @param bindingResult 유효성 검사 오류를 포함하는 객체
     * @return 사용자 ID와 이름
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(@ModelAttribute @Valid JoinReqDto joinReqDto, BindingResult bindingResult){
        return new ResponseEntity<>(ApiUtil.success(userService.join(joinReqDto)), HttpStatus.CREATED);
    }

    /**
     * 로그인
     *
     * @param loginReqDto 로그인 정보를 담고 있는 DTO
     * @param bindingResult 유효성 검사 오류를 포함하는 객체
     * @param request HttpServletRequest 객체로 로그인 세션을 생성하는 데 사용
     * @return 사용자 ID와 이름
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginReqDto loginReqDto, BindingResult bindingResult,
                                   HttpServletRequest request){
        return new ResponseEntity<>(ApiUtil.success(userService.login(loginReqDto, request)), HttpStatus.OK);
    }

}
