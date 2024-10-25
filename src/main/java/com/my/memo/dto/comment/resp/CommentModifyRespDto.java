package com.my.memo.dto.comment.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class CommentModifyRespDto {

    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime modifiedAt;

    public CommentModifyRespDto(Comment comment) {
        this.content = comment.getContent();
        this.modifiedAt = comment.getLastModifiedAt();
    }
}