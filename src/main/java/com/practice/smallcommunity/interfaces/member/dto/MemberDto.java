package com.practice.smallcommunity.interfaces.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberDto {

    private String nickname;
}