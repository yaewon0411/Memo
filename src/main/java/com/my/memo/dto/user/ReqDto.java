package com.my.memo.dto.user;

import com.my.memo.domain.user.User;
import com.my.memo.util.CustomPasswordUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.internal.build.AllowNonPortable;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    public static class UserModifyReqDto{

        @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해야 합니다 ")
        @Pattern(regexp = "^(?!\\s*$)[a-zA-Z가-힣\\s]+$", message = "이름은 영문 또는 한글만 입력 가능합니다")
        private String name;

        @Email(message = "이메일 형식이 올바르지 않습니다")
        private String email;

        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,15}$", message = "영문자와 숫자를 포함하여 6자 이상 15자 이하여야 합니다")
        private String password; //영문자+숫자 조합 최소 6~15자

        public String getEncodedPassword(){
            return CustomPasswordUtil.encode(this.password);
        }

    }

    @NoArgsConstructor
    @Getter
    public static class LoginReqDto{
        @Email(message = "이메일 형식이 올바르지 않습니다")
        @NotBlank(message = "이메일을 입력해야 합니다")
        private String email;

        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,15}$", message = "영문자와 숫자를 포함하여 6자 이상 15자 이하여야 합니다")
        @NotBlank(message = "비밀번호를 입력해야 합니다")
        private String password;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class JoinReqDto{

        @NotBlank(message = "이름을 입력해야 합니다")
        @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해야 합니다 ")
        @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름은 영문 또는 한글만 입력 가능합니다")
        private String name;

        @Email(message = "이메일 형식이 올바르지 않습니다")
        @NotBlank(message = "이메일을 입력해야 합니다")
        private String email;

        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,15}$", message = "영문자와 숫자를 포함하여 6자 이상 15자 이하여야 합니다")
        @NotBlank(message = "비밀번호를 입력해야 합니다")
        private String password; //영문자+숫자 조합 최소 6~15자

        public User toEntity(){
            return User.builder()
                    .name(this.name)
                    .email(this.email)
                    .password(CustomPasswordUtil.encode(this.password))
                    .build();
        }
    }
}
