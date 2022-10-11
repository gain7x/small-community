package com.practice.smallcommunity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableRedisRepositories
@EnableAsync
@EnableJpaAuditing
@Configuration
public class AppConfig {

}
