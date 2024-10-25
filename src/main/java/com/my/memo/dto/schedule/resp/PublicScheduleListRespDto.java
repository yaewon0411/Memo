package com.my.memo.dto.schedule.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.memo.domain.schedule.dto.ScheduleWithCommentAndUserCountsDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 스케줄 id
 * 작성자명
 * 생성일
 * 수정일
 * content
 *
 * 까지만 내보내기
 * */
@NoArgsConstructor
@Getter
public class PublicScheduleListRespDto {
    private List<ScheduleRespDto> scheduleRespDtoList;
    private boolean hasNextPage;
    private int totalCounts;
    private int totalPages;
    private int currentPage;

    public PublicScheduleListRespDto(List<ScheduleWithCommentAndUserCountsDto> scheduleList, boolean hasNextPage, int totalPublicSchedules, int totalPages, int currentPage) {
        this.hasNextPage = hasNextPage;
        this.scheduleRespDtoList = scheduleList.stream()
                .map(ScheduleRespDto::new)
                .collect(Collectors.toList());
        this.totalCounts = totalPublicSchedules;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    @NoArgsConstructor
    @Getter
    public static class ScheduleRespDto {
        private Long id;
        private String name;
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
            this.name = scheduleDto.getSchedule().getUser().getName();
            this.createdAt = scheduleDto.getSchedule().getCreatedAt();
            this.modifiedAt = scheduleDto.getSchedule().getLastModifiedAt();
            this.assignedUserCnt = (int) scheduleDto.getAssignedUserCnt().longValue();
            this.commentCnt = (int) scheduleDto.getCommentCnt().longValue();
        }
    }
}