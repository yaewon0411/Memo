package com.my.memo.web;

import com.my.memo.dto.user.ReqDto;
import com.my.memo.service.UserService;
import com.my.memo.util.api.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.my.memo.dto.user.ReqDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping
    public ResponseEntity<?> join(@RequestBody JoinReqDto joinReqDto){
        return new ResponseEntity<>(ApiUtil.success(userService.join(joinReqDto)), HttpStatus.CREATED);
    }


}
