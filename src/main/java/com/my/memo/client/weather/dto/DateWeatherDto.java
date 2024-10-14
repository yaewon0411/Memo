package com.my.memo.client.weather.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DateWeatherDto {
    private String date;
    private String weather;
}