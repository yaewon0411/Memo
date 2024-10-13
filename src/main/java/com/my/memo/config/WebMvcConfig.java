package com.my.memo.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정을 위한 구성 클래스입니다
 * <p>
 * 인증 관련 인터셉터인 `AuthInterceptor`가 지정된 경로에서 동작하도록 설정합니다
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.debug("디버그: authInterceptor 등록");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");
    }

}
