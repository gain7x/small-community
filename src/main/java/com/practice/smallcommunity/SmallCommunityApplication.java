package com.practice.smallcommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SmallCommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmallCommunityApplication.class, args);
	}

}
