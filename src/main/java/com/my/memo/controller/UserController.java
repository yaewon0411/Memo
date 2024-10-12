package com.my.memo.controller;

import com.my.memo.config.jwt.RequireAuth;
import com.my.memo.domain.user.Role;
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
import static com.my.memo.dto.user.RespDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


//    @RequireAuth(role = Role.USER)
//    @PostMapping("/s/logout")
//    public ResponseEntity<ApiResult<UserLogoutRespDto>> logout(HttpServletRequest request) {
//        return new ResponseEntity<>(ApiResult.success(userService.logout(request)), HttpStatus.OK);
//    }


    @RequireAuth(role = Role.USER)
    @DeleteMapping("/s/users")
    public ResponseEntity<ApiResult<UserDeleteRespDto>> deleteUser(HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(userService.deleteUser(request)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @GetMapping("/s/users")
    public ResponseEntity<ApiResult<UserRespDto>> getUserInfo(HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(userService.getUserInfo(request)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @PatchMapping("/s/users")
    public ResponseEntity<ApiResult<UserModifyRespDto>> updateUser(@RequestBody @Valid UserModifyReqDto userModifyReqDto,
                                                                   HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(userService.updateUser(userModifyReqDto, request)), HttpStatus.OK);
    }


    @PostMapping("/join")
    public ResponseEntity<ApiResult<JoinRespDto>> join(@ModelAttribute @Valid JoinReqDto joinReqDto) {
        return new ResponseEntity<>(ApiResult.success(userService.join(joinReqDto)), HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResult<LoginRespDto>> login(@RequestBody @Valid LoginReqDto loginReqDto,
                                                         HttpServletResponse response, HttpServletRequest request) {
        return new ResponseEntity<>(ApiResult.success(userService.login(loginReqDto, response, request)), HttpStatus.OK);
    }

}
