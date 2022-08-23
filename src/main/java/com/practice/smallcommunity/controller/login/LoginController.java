package com.practice.smallcommunity.controller.login;

import com.practice.smallcommunity.controller.login.dto.LoginRequest;
import com.practice.smallcommunity.controller.login.dto.LoginDto;
import com.practice.smallcommunity.service.login.LoginTokenService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class LoginController {

    private final LoginTokenService loginTokenService;

    @PostMapping("/auth")
    public LoginDto auth(@Valid @RequestBody LoginRequest loginRequest) {
        String token = loginTokenService.issuance(loginRequest.getUsername(),
            loginRequest.getPassword());

        return new LoginDto(token);
    }
}
