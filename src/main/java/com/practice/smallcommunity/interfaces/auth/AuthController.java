package com.practice.smallcommunity.interfaces.auth;

import com.practice.smallcommunity.application.AuthService;
import com.practice.smallcommunity.application.dto.AuthDto;
import com.practice.smallcommunity.interfaces.BaseResponse;
import com.practice.smallcommunity.interfaces.auth.dto.LoginRequest;
import com.practice.smallcommunity.interfaces.auth.dto.LoginResponse;
import java.util.Date;
import javax.servlet.http.Cookie;
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

    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final AuthService authService;
    private final AuthMapper mapper;

    @PostMapping
    public BaseResponse<LoginResponse> auth(@Valid @RequestBody LoginRequest loginRequest,
        HttpServletResponse response) {
        AuthDto authDto = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        setRefreshTokenCookie(authDto.getRefreshToken(), authDto.getRefreshTokenExpires(),
            response);

        return BaseResponse.Ok(mapper.toResponse(authDto));
    }

    @PostMapping("/refresh")
    public BaseResponse<LoginResponse> refresh(
        @CookieValue(REFRESH_TOKEN_COOKIE) String refreshToken, HttpServletResponse response) {
        AuthDto authDto = authService.refresh(refreshToken);
        setRefreshTokenCookie(authDto.getRefreshToken(), authDto.getRefreshTokenExpires(),
            response);

        return BaseResponse.Ok(mapper.toResponse(authDto));
    }

    @PostMapping("/logout")
    public void logout(@CookieValue(REFRESH_TOKEN_COOKIE) String refreshToken, HttpServletResponse response) {
        authService.deleteRefreshToken(refreshToken);

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, null);
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(refreshTokenCookie);
    }

    private void setRefreshTokenCookie(String refreshToken, Date expires,
        HttpServletResponse response) {
        int refreshTokenExpirationTime =
            (int) ((expires.getTime() - new Date().getTime()) / 1000);

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(refreshTokenExpirationTime);
        refreshTokenCookie.setPath("/");

        response.addCookie(refreshTokenCookie);
    }
}
