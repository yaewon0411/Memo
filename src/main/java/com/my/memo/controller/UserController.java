package com.my.memo.controller;

import com.my.memo.service.UserService;
import com.my.memo.util.api.ApiUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;

import static com.my.memo.dto.user.ReqDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    //로그아웃
    @PostMapping("/s/logout")
    public ResponseEntity<?>logout(HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.logout(session)), HttpStatus.OK);
    }

    //유저 삭제
    @DeleteMapping("/s")
    public ResponseEntity<?> deleteUser(HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.deleteUser(session)), HttpStatus.OK);
    }

    //유저 정보 조회
    @GetMapping("/s")
    public ResponseEntity<?> getUserInfo(HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.getUserInfo(session)), HttpStatus.OK);
    }

    //정보 수정
    @PatchMapping("/s")
    public ResponseEntity<?> modifyUserInfo(@RequestBody @Valid UserModifyReqDto userModifyReqDto, BindingResult bindingResult,
                                            HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.modifyUserInfo(userModifyReqDto, session)), HttpStatus.OK);
    }

    //회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@ModelAttribute @Valid JoinReqDto joinReqDto, BindingResult bindingResult){
        return new ResponseEntity<>(ApiUtil.success(userService.join(joinReqDto)), HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginReqDto loginReqDto, BindingResult bindingResult,
                                   HttpSession session){
        return new ResponseEntity<>(ApiUtil.success(userService.login(loginReqDto,session )), HttpStatus.OK);
    }

}
