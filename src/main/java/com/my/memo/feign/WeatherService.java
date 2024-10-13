package com.my.memo.feign;

import com.my.memo.ex.CustomApiException;
import com.my.memo.feign.dto.DateWeatherDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherFeignClient weatherFeignClient;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public String getTodayWeather(String today) {
        try {
            return weatherFeignClient.getDateWeatherInfo().stream()
                    .filter(dateWeatherDto -> dateWeatherDto.getDate().equals(today))
                    .map(DateWeatherDto::getWeather)
                    .findFirst()
                    .orElse("날씨 정보가 없네용 🥹");

        } catch (Exception e) {
            log.error("날씨 정보 조회 중 오류 발생: " + e.getMessage());
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "날씨 정보 조회 중 오류 발생");
        }
    }

}
