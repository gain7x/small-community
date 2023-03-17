package com.practice.smallcommunity.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.nio.charset.StandardCharsets;

@Getter
@ConfigurationProperties("jwt")
@Validated
public class JwtProperties {

    @NotEmpty
    private final byte[] secretKey;

    @Min(1)
    private final long accessTokenExpirationMinutes;

    @Min(1)
    private final long refreshTokenExpirationHours;

    @ConstructorBinding
    public JwtProperties(String secretKey,
                         @DefaultValue("30") long accessTokenExpirationMinutes,
                         @DefaultValue("24") long refreshTokenExpirationHours) {
        this.secretKey = secretKey.getBytes(StandardCharsets.UTF_8);
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationHours = refreshTokenExpirationHours;
    }
}
