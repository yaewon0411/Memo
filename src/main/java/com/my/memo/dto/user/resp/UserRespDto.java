package com.my.memo.dto.user.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.my.memo.domain.user.Role;
import com.my.memo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class UserRespDto {
    private String name;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime joinDay; //가입일

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String role;

    public UserRespDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.joinDay = user.getCreatedAt();
        this.role = user.getRole().equals(Role.ADMIN) ? user.getRole().toString() : null;
    }
}
