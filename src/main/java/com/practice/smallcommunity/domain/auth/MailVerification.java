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
    private String mail;

    @TimeToLive
    private long ttl;

    @Builder
    public MailVerification(String key, String mail, long ttl) {
        this.key = key;
        this.mail = mail;
        this.ttl = ttl;
    }
}
