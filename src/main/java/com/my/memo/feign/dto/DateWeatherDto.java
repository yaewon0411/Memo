package com.my.memo.feign.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DateWeatherDto {
    private String date;
    private String weather;
}