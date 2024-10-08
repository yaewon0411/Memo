package com.my.memo.dto.comment;

import com.my.memo.domain.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class CommentCreateRespDto {
        private Long id;
        private String content;

        public CommentCreateRespDto(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
        }
    }
}
