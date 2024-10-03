package com.my.memo.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정을 위한 구성 클래스입니다
 *
 * 인증 관련 인터셉터인 `AuthInterceptor`가 지정된 경로에서 동작하도록 설정합니다
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final static Logger log = LoggerFactory.getLogger(WebMvcConfig.class);

    /**
     * `AuthInterceptor`를 인터셉터 체인에 등록하고, 특정 경로에 대해 인터셉터가 적용되도록 설정합니다
     *
     * `/api/s/**` 경로에 대해 `AuthInterceptor`가 적용되며
     *
     * 회원가입(`/api/join`), 로그인(`/api/login`),
     * 공개 일정 관련 경로(`/api/schedules/**`, `/api/s/schedules/*`)는 인터셉터에서 제외됩니다
     *
     * @param registry InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.debug("디버그: authInterceptor 등록");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/s/**")
                .excludePathPatterns("/api/join", "/api/login", "/api/schedules/**", "/api/s/schedules/*");
    }

}
