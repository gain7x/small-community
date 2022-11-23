package com.practice.smallcommunity.interfaces.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
public class OAuth2RegisterRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String key;

    @NotBlank
    @Length(min = 2, max = 12)
    private String nickname;
}
