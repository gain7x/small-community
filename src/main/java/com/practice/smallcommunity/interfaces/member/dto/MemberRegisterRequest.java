package com.practice.smallcommunity.interfaces.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
public class MemberRegisterRequest {

    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 15)
    private String password;

    @NotBlank
    @Length(min = 2, max = 12)
    private String nickname;
}
