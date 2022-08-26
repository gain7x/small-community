package com.practice.smallcommunity.interfaces.login.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    @NotBlank
    @Length(min = 4, max = 15)
    private String username;

    @NotBlank
    @Length(min = 8, max = 15)
    private String password;
}
