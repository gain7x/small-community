package com.practice.smallcommunity.interfaces.login;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.interfaces.login.dto.LoginRequest;
import com.practice.smallcommunity.interfaces.login.dto.LoginResponse;
import com.practice.smallcommunity.application.LoginService;
import com.practice.smallcommunity.security.JwtTokenProvider;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public LoginResponse auth(@Valid @RequestBody LoginRequest loginRequest) {
        Member member = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtTokenProvider.createToken(member);

        return LoginResponse.builder()
            .accessToken(token)
            .email(member.getEmail())
            .nickname(member.getNickname())
            .lastPasswordChange(member.getLastPasswordChange())
            .build();
    }
}
