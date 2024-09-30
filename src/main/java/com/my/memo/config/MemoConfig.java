package com.my.memo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class MemoConfig {

    private final static Logger log = LoggerFactory.getLogger(MemoConfig.class);
}
