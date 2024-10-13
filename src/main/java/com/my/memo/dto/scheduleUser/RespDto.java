package com.my.memo.dto.scheduleUser;

import com.my.memo.domain.schedule.Schedule;
import com.my.memo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class UserAssignRespDto {
        private Long scheduleId;

        private List<UserDto> userDtoList;

        public UserAssignRespDto(Schedule schedule, List<User> assignedUserList) {
            this.scheduleId = schedule.getId();
            this.userDtoList = assignedUserList.stream().map(UserDto::new).toList();
        }

        @NoArgsConstructor
        @Getter
        public static class UserDto {
            private String name;
            private Long id;
            public UserDto(User user) {
                this.name = user.getName();
                this.id = user.getId();
            }
        }
    }
}
