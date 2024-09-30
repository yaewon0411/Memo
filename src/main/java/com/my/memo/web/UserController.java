package com.my.memo.web;

import com.my.memo.dto.user.ReqDto;
import com.my.memo.service.UserService;
import com.my.memo.util.api.ApiUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.user.ReqDto.*;
import static com.my.memo.service.UserService.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

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
