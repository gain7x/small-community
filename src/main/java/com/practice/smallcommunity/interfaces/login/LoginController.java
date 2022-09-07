package com.practice.smallcommunity.interfaces.login;

import com.practice.smallcommunity.application.LoginService;
import com.practice.smallcommunity.application.dto.LoginDto;
import com.practice.smallcommunity.interfaces.BaseResponse;
import com.practice.smallcommunity.interfaces.login.dto.LoginRequest;
import com.practice.smallcommunity.interfaces.login.dto.LoginResponse;
import com.practice.smallcommunity.interfaces.login.dto.RefreshRequest;
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
    private final LoginMapper mapper;

    @PostMapping
    public BaseResponse<LoginResponse> auth(@Valid @RequestBody LoginRequest loginRequest) {
        LoginDto loginDto = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return BaseResponse.Ok(mapper.toResponse(loginDto));
    }

    @PostMapping("/refresh")
    public BaseResponse<LoginResponse> refresh(@Valid @RequestBody RefreshRequest dto) {
        LoginDto loginDto = loginService.refresh(dto.getAccessToken(), dto.getRefreshToken());
        return BaseResponse.Ok(mapper.toResponse(loginDto));
    }
}
