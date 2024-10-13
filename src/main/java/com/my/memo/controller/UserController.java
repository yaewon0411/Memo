package com.my.memo.controller;

import com.my.memo.config.jwt.RequireAuth;
import com.my.memo.domain.user.Role;
import com.my.memo.domain.user.User;
import com.my.memo.service.UserService;
import com.my.memo.util.api.ApiResult;
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


    @RequireAuth(role = Role.USER)
    @DeleteMapping("/s/users")
    public ResponseEntity<ApiResult<UserDeleteRespDto>> deleteUser(User user) {
        return new ResponseEntity<>(ApiResult.success(userService.deleteUser(user)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @GetMapping("/s/users")
    public ResponseEntity<ApiResult<UserRespDto>> getUserInfo(User user) {
        return new ResponseEntity<>(ApiResult.success(userService.getUserInfo(user)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @PatchMapping("/s/users")
    public ResponseEntity<ApiResult<UserModifyRespDto>> updateUser(@RequestBody @Valid UserModifyReqDto userModifyReqDto,
                                                                   User user) {
        return new ResponseEntity<>(ApiResult.success(userService.updateUser(userModifyReqDto, user)), HttpStatus.OK);
    }


    @PostMapping("/join")
    public ResponseEntity<ApiResult<JoinRespDto>> join(@ModelAttribute @Valid JoinReqDto joinReqDto) {
        return new ResponseEntity<>(ApiResult.success(userService.join(joinReqDto)), HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResult<LoginRespDto>> login(@RequestBody @Valid LoginReqDto loginReqDto,
                                                         HttpServletResponse response) {
        return new ResponseEntity<>(ApiResult.success(userService.login(loginReqDto, response)), HttpStatus.OK);
    }

}
