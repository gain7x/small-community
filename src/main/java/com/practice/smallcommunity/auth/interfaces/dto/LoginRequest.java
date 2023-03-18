package com.practice.smallcommunity.auth.interfaces.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 15)
    private String password;
}
