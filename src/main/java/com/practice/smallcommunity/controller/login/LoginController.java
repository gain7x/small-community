package com.practice.smallcommunity.controller.login;

import com.practice.smallcommunity.controller.login.dto.LoginRequest;
import com.practice.smallcommunity.controller.login.dto.LoginResponse;
import com.practice.smallcommunity.service.login.LoginTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginTokenService loginTokenService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String token = loginTokenService.issuance(loginRequest.getUsername(),
            loginRequest.getPassword());

        return new LoginResponse(token);
    }
}
