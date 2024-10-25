package com.my.memo.dto.comment;

import com.my.memo.domain.comment.Comment;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    public static class CommentModifyReqDto {
        @NotBlank(message = "코멘트를 작성해야 합니다")
        @Length(min = 1, max = 512, message = "1자 이상 512자 이하로 입력해야 합니다")
        private String content;
    }

    @NoArgsConstructor
    @Getter
    public static class CommentCreateReqDto {

        @Length(min = 1, max = 512, message = "1자 이상 512자 이하로 입력해야 합니다")
        @NotBlank(message = "코멘트를 입력해야 합니다")
        private String content;

        public Comment toEntity(User user, Schedule schedule) {
            return Comment.builder()
                    .content(this.content)
                    .user(user)
                    .schedule(schedule)
                    .build();
        }
    }
}
