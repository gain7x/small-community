package com.practice.smallcommunity.auth.application.dto;

import com.practice.smallcommunity.member.domain.Member;
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
