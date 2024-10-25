package com.my.memo.dto.comment.resp;

import com.my.memo.domain.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
public class CommentCreateRespDto {
    private Long id;
    @Length(min = 1, max = 512, message = "1자 이상 512자 이하로 입력해야 합니다")
    private String content;

    public CommentCreateRespDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
    }
}