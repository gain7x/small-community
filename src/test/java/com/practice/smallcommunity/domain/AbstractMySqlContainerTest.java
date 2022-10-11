package com.practice.smallcommunity.domain;

import org.testcontainers.containers.MySQLContainer;

public abstract class AbstractMySqlContainerTest {

    static final String IMAGE = "mysql:8.0.30";
    static final MySQLContainer<?> MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer<>(IMAGE)
            .withDatabaseName("S_COMM")
            .withUsername("test")
            .withPassword("test")
            .withExposedPorts(3306);
        MY_SQL_CONTAINER.start();
    }
}
