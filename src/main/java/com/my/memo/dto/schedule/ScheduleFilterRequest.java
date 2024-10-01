package com.my.memo.dto.schedule;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ScheduleFilterRequest {

    @Pattern(regexp = "^(30m|1h|1d|1w|1m|3m|6m|\\d{4}-\\d{2}-\\d{2})?$", message = "유효하지 않은 modifiedAt 값입니다.")
    @Nullable
    private String modifiedAt;
    @Nullable
    private String authorName;

    @Nullable
    private Long limit;

    @Nullable
    private Long page;

    public long getPage(){
        if(this.page==null)
            return 0;
        else return this.page;
    }
    public long getLimit(){
        if(this.limit==null) return 10;
        else return this.limit;
    }
}
