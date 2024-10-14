package com.my.memo.client;

import com.my.memo.client.dto.DateWeatherDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "weatherFeignClient", url = "https://f-api.github.io/f-api/weather.json")
public interface WeatherFeignClient {
    @GetMapping
    List<DateWeatherDto> getDateWeatherInfo();
}
