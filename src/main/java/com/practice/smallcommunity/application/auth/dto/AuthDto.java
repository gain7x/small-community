package com.practice.smallcommunity.application.auth.dto;

import com.practice.smallcommunity.domain.member.Member;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthDto {

    private String accessToken;
    private Date accessTokenExpires;

    private String refreshToken;
    private Date refreshTokenExpires;

    private Member member;
}
