package com.my.memo.controller;

import com.my.memo.config.auth.jwt.JwtProvider;
import com.my.memo.config.auth.jwt.RequireAuth;
import com.my.memo.config.user.UserId;
import com.my.memo.domain.user.Role;
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
    private final JwtProvider jwtProvider;


    @RequireAuth(role = Role.USER)
    @DeleteMapping("/users")
    public ResponseEntity<ApiResult<UserDeleteRespDto>> deleteUser(@UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(userService.deleteUser(userId)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @GetMapping("/users")
    public ResponseEntity<ApiResult<UserRespDto>> getUserInfo(@UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(userService.getUserInfo(userId)), HttpStatus.OK);
    }


    @RequireAuth(role = Role.USER)
    @PatchMapping("/users")
    public ResponseEntity<ApiResult<UserModifyRespDto>> updateUser(@RequestBody @Valid UserModifyReqDto userModifyReqDto,
                                                                   @UserId Long userId) {
        return new ResponseEntity<>(ApiResult.success(userService.updateUser(userModifyReqDto, userId)), HttpStatus.OK);
    }


    @PostMapping("/join")
    public ResponseEntity<ApiResult<JoinRespDto>> join(@ModelAttribute @Valid JoinReqDto joinReqDto) {
        return new ResponseEntity<>(ApiResult.success(userService.join(joinReqDto)), HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResult<LoginRespDto>> login(@RequestBody @Valid LoginReqDto loginReqDto,
                                                         HttpServletResponse response) {

        LoginRespDto result = userService.login(loginReqDto);
        jwtProvider.addJwtToHeader(result.getJwt(), response);
        return new ResponseEntity<>(ApiResult.success(result), HttpStatus.OK);
    }

}
