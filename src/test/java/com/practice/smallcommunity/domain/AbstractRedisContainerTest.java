package com.practice.smallcommunity.domain;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public abstract class AbstractRedisContainerTest {

    static final String IMAGE = "redis:7.0.5-alpine";
    static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(IMAGE)
            .withExposedPorts(6379);
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.port", () -> Integer.toString(REDIS_CONTAINER.getMappedPort(6379)));
    }
}
