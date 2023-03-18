package com.practice.smallcommunity.auth.interfaces;

import static com.practice.smallcommunity.auth.interfaces.AuthUtil.setRefreshTokenCookie;

import com.practice.smallcommunity.auth.application.AuthService;
import com.practice.smallcommunity.auth.application.LoginService;
import com.practice.smallcommunity.auth.application.dto.AuthDto;
import com.practice.smallcommunity.auth.domain.Login;
import com.practice.smallcommunity.common.interfaces.dto.BaseResponse;
import com.practice.smallcommunity.auth.interfaces.dto.LoginRequest;
import com.practice.smallcommunity.auth.interfaces.dto.LoginResponse;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginService loginService;
    private final AuthService authService;
    private final AuthMapper mapper;

    @PostMapping
    public BaseResponse<LoginResponse> auth(@Valid @RequestBody LoginRequest loginRequest,
        HttpServletResponse response) {
        Login login = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        AuthDto authDto = authService.createAuthentication(login.getMember());
        setRefreshTokenCookie(response, authDto.getRefreshToken(), authDto.getRefreshTokenExpires());

        return BaseResponse.Ok(mapper.toResponse(authDto));
    }

    @PostMapping("/refresh")
    public BaseResponse<LoginResponse> refresh(
        @CookieValue(AuthUtil.REFRESH_TOKEN_COOKIE) String refreshToken, HttpServletResponse response) {
        AuthDto authDto = authService.refresh(refreshToken);
        setRefreshTokenCookie(response, authDto.getRefreshToken(), authDto.getRefreshTokenExpires());

        return BaseResponse.Ok(mapper.toResponse(authDto));
    }

    @PostMapping("/logout")
    public void logout(@CookieValue(AuthUtil.REFRESH_TOKEN_COOKIE) String refreshToken, HttpServletResponse response) {
        authService.deleteRefreshToken(refreshToken);
        AuthUtil.deleteRefreshTokenCookie(response);
    }
}
