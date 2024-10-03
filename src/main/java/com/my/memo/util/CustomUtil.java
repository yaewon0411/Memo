package com.my.memo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 유틸리티 클래스입니다
 * 주로 문자열과 날짜 타입 간의 형 변환을 수행합니다
 */
public class CustomUtil {
    private static final DateTimeFormatter SCHEDULE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String convertToJson(Object object) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.writeValueAsString(object);
    }

    public static LocalDateTime scheduleTimeToLocalDateTime(String str){
        return LocalDateTime.parse(str, SCHEDULE_FORMATTER);
    }

    public static String localDateTimeToScheduleTime(LocalDateTime localDateTime){
        return localDateTime.format(SCHEDULE_FORMATTER);
    }
}
