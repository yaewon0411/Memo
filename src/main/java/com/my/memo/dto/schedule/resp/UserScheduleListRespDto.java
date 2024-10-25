package com.my.memo.dto.schedule.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.schedule.dto.ScheduleWithCommentAndUserCountsDto;
import com.my.memo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class UserScheduleListRespDto {

    private List<ScheduleRespDto> scheduleRespDtoList;
    private boolean hasNextPage;
    private String name;
    private int totalCounts;
    private int totalPages;
    private int currentPage;

    public UserScheduleListRespDto(List<ScheduleWithCommentAndUserCountsDto> scheduleList, boolean hasNextPage, User user, int totalCounts, int totalPages, int currentPage) {
        this.hasNextPage = hasNextPage;
        this.scheduleRespDtoList = scheduleList.stream()
                .map(s -> new ScheduleRespDto(s))
                .collect(Collectors.toList());
        this.name = user.getName();
        this.totalCounts = totalCounts;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    @NoArgsConstructor
    @Getter
    public static class ScheduleRespDto {
        private Long id;
        private String content;
        private Boolean isPublic;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime modifiedAt;
        private int commentCnt;
        private int assignedUserCnt;


        public ScheduleRespDto(ScheduleWithCommentAndUserCountsDto scheduleDto) {
            this.content = scheduleDto.getSchedule().getContent();
            this.isPublic = scheduleDto.getSchedule().isPublic();
            this.id = scheduleDto.getSchedule().getId();
            this.createdAt = scheduleDto.getSchedule().getCreatedAt();
            this.modifiedAt = scheduleDto.getSchedule().getLastModifiedAt();
            this.assignedUserCnt = (int) scheduleDto.getAssignedUserCnt().longValue();
            this.commentCnt = (int) scheduleDto.getCommentCnt().longValue();
        }
    }

}
