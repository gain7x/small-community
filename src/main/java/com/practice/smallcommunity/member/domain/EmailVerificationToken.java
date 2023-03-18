package com.practice.smallcommunity.member.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("EmailVerificationToken")
public class EmailVerificationToken {

    @Id
    private String email;
    private String key;

    @TimeToLive
    private long expirationSeconds;

    @Builder
    public EmailVerificationToken(String email, String key, long expirationSeconds) {
        this.email = email;
        this.key = key;
        this.expirationSeconds = expirationSeconds;
    }
}
