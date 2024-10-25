package com.my.memo.dto.comment.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
public class CommentModifyReqDto {
    @NotBlank(message = "코멘트를 작성해야 합니다")
    @Length(min = 1, max = 512, message = "1자 이상 512자 이하로 입력해야 합니다")
    private String content;
}
