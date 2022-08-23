package com.practice.smallcommunity.controller.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
public class MemberRegisterDto {

    @NotBlank
    @Length(min = 4, max = 15)
    private String username;

    @NotBlank
    @Length(min = 8, max = 15)
    private String password;

    @Email
    private String email;
}
