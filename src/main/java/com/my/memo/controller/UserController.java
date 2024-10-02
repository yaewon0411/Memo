package com.my.memo.controller;

import com.my.memo.service.UserService;
import com.my.memo.util.api.ApiUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.user.ReqDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    //로그아웃
    @PostMapping("/s/users/logout")
    public ResponseEntity<?>logout(HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.logout(session)), HttpStatus.OK);
    }

    //유저 삭제
    @DeleteMapping("/s/users")
    public ResponseEntity<?> deleteUser(HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.deleteUser(session)), HttpStatus.OK);
    }

    //유저 정보 조회
    @GetMapping("/s/users")
    public ResponseEntity<?> getUserInfo(HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.getUserInfo(session)), HttpStatus.OK);
    }

    //정보 수정
    @PatchMapping("/s/users")
    public ResponseEntity<?> modifyUserInfo(@RequestBody @Valid UserModifyReqDto userModifyReqDto, BindingResult bindingResult,
                                            HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.modifyUserInfo(userModifyReqDto, session)), HttpStatus.OK);
    }

    //회원가입
    @PostMapping("/users/join")
    public ResponseEntity<?> join(@ModelAttribute @Valid JoinReqDto joinReqDto, BindingResult bindingResult){
        return new ResponseEntity<>(ApiUtil.success(userService.join(joinReqDto)), HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginReqDto loginReqDto, BindingResult bindingResult,
                                   HttpServletRequest request){
        return new ResponseEntity<>(ApiUtil.success(userService.login(loginReqDto, request)), HttpStatus.OK);
    }

}
