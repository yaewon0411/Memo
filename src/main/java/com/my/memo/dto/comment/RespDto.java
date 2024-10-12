package com.my.memo.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public class RespDto {
    @NoArgsConstructor
    @Getter
    public static class CommentDeleteRespDto {
        private Long id;
        private Boolean deleted;

        public CommentDeleteRespDto(Long id, Boolean deleted) {
            this.id = id;
            this.deleted = deleted;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CommentModifyRespDto {

        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime modifiedAt;

        public CommentModifyRespDto(Comment comment) {
            this.content = comment.getContent();
            this.modifiedAt = comment.getLastModifiedAt();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CommentCreateRespDto {
        private Long id;
        @Length(min = 1, max = 512, message = "1자 이상 512자 이하로 입력해야 합니다")
        private String content;

        public CommentCreateRespDto(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
        }
    }
}
