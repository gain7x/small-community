package com.practice.smallcommunity.interfaces.login.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    private String email;
    private String nickname;
    private LocalDateTime lastPasswordChange;
}
