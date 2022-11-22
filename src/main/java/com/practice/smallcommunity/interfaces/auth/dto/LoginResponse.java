package com.practice.smallcommunity.interfaces.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

    @JsonInclude(Include.NON_NULL)
    private Boolean admin;
}
