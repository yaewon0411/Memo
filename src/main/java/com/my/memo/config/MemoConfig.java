package com.my.memo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Clock;

@Configuration
@EnableJpaAuditing
public class MemoConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
