package com.practice.smallcommunity.interfaces.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EmailVerificationRequest {

    @Email
    private String email;

    @NotBlank
    private String key;

    @NotBlank
    private String redirectUri;
}
