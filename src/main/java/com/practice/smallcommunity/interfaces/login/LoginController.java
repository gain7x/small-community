package com.practice.smallcommunity.interfaces.login;

import com.practice.smallcommunity.interfaces.login.dto.LoginRequest;
import com.practice.smallcommunity.interfaces.login.dto.LoginResponse;
import com.practice.smallcommunity.application.LoginTokenService;
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

    private final LoginTokenService loginTokenService;

    @PostMapping
    public LoginResponse auth(@Valid @RequestBody LoginRequest loginRequest) {
        String token = loginTokenService.issuance(loginRequest.getEmail(),
            loginRequest.getPassword());

        return new LoginResponse(token);
    }
}
