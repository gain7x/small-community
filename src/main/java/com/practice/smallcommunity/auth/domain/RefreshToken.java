package com.practice.smallcommunity.auth.domain;

import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

/**
 * 리프레시 토큰 엔티티입니다.
 */
@Getter
@RedisHash
public class RefreshToken {

    @Id
    private String token;
    private long memberId;

    @TimeToLive(unit = TimeUnit.HOURS)
    private long expirationHours;

    @Builder
    public RefreshToken(String token, long memberId, long expirationHours) {
        this.token = token;
        this.memberId = memberId;
        this.expirationHours = expirationHours;
    }
}
