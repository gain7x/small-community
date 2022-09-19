package com.practice.smallcommunity.security.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDto {

    private final String token;
    private final Date expires;

    @Builder
    public TokenDto(String token, Date expires) {
        this.token = token;
        this.expires = expires;
    }
}
