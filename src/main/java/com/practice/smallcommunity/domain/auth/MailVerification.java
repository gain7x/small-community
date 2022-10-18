package com.practice.smallcommunity.domain.auth;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("VerificationNumber")
public class MailVerification {

    @Id
    private String key;
    private String email;

    @TimeToLive
    private long expirationSeconds;

    @Builder
    public MailVerification(String key, String email, long expirationSeconds) {
        this.key = key;
        this.email = email;
        this.expirationSeconds = expirationSeconds;
    }
}
