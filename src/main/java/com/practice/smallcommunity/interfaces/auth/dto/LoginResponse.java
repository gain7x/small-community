package com.practice.smallcommunity.interfaces.auth.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private long accessTokenExpires;

    private String refreshToken;
    private long refreshTokenExpires;

    private Long memberId;
    private String email;
    private String nickname;
    private LocalDateTime lastPasswordChange;
}
