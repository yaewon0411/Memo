package com.my.memo.controller;

import com.my.memo.config.jwt.RequireAuth;
import com.my.memo.service.UserService;
import com.my.memo.util.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.user.ReqDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    @RequireAuth
    @PostMapping("/s/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(userService.logout(request)), HttpStatus.OK);
    }


    @RequireAuth
    @DeleteMapping("/s/users")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(userService.deleteUser(request)), HttpStatus.OK);
    }


    @RequireAuth
    @GetMapping("/s/users")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(userService.getUserInfo(request)), HttpStatus.OK);
    }


    @RequireAuth
    @PatchMapping("/s/users")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserModifyReqDto userModifyReqDto,
                                        HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(userService.updateUser(userModifyReqDto, request)), HttpStatus.OK);
    }


    @PostMapping("/join")
    public ResponseEntity<?> join(@ModelAttribute @Valid JoinReqDto joinReqDto) {
        return new ResponseEntity<>(ApiResult.success(userService.join(joinReqDto)), HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginReqDto loginReqDto,
                                   HttpServletResponse response, HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(userService.login(loginReqDto, response, request)), HttpStatus.OK);
    }

}
