package com.my.memo.dto.user.req;

import com.my.memo.dto.user.req.valid.name.IsKoreanOrEnglish;
import com.my.memo.util.CustomPasswordUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
public class UserModifyReqDto {

    @IsKoreanOrEnglish
    @Length(min = 1, max = 12, message = "이름은 1자 이상 12자 이하여야 합니다")
    private String name;

    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,15}$", message = "비밀번호는 영문자와 숫자를 포함하여 6자 이상 15자 이하여야 합니다")
    private String password; //영문자+숫자 조합 최소 6~15자


    public String getEncodedPassword() {
        return CustomPasswordUtil.encode(this.password);
    }

}
