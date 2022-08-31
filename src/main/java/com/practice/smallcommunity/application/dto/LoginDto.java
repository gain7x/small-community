package com.practice.smallcommunity.application.dto;

import com.practice.smallcommunity.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginDto {

    private String accessToken;
    private String refreshToken;
    private Member member;
}
