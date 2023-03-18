package com.practice.smallcommunity.auth.domain.oauth2;

import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("SocialUserRegistrationToken")
public class OAuth2RegistrationToken {

    @Id
    private String email;
    private String key;
    private String username;
    private OAuth2Platform platform;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private long expirationMinutes;

    @Builder
    public OAuth2RegistrationToken(String email, String key, String username, OAuth2Platform platform,
        long expirationMinutes) {
        this.key = key;
        this.email = email;
        this.username = username;
        this.platform = platform;
        this.expirationMinutes = expirationMinutes;
    }
}
