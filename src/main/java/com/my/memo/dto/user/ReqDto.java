package com.my.memo.dto.user;

import com.my.memo.domain.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ReqDto {
    @NoArgsConstructor
    @Getter
    public static class JoinReqDto{

        @NotBlank(message = "이름을 입력해야 합니다")
        @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해야 합니다 ")
        @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름은 영문 또는 한글만 입력 가능합니다")
        private String name;

        @Email
        @NotBlank
        private String email;

        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,15}$", message = "영문자와 숫자를 포함하여 6자 이상 15자 이하여야 합니다")
        @NotBlank
        private String password; //영문자+숫자 조합 최소 6~15자

        public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder){
            return User.builder()
                    .name(this.name)
                    .email(this.email)
                    .password(bCryptPasswordEncoder.encode(this.password))
                    .build();
        }
    }
}
