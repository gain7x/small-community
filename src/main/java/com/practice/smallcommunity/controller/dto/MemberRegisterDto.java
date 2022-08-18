package com.practice.smallcommunity.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberRegisterDto {

    private String username;
    private String password;
    private String email;
}
