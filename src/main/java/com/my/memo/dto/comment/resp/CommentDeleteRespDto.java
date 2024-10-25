package com.my.memo.dto.comment.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentDeleteRespDto {
    private Long id;
    private Boolean deleted;

    public CommentDeleteRespDto(Long id, Boolean deleted) {
        this.id = id;
        this.deleted = deleted;
    }
}