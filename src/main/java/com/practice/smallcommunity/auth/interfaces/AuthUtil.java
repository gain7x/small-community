package com.practice.smallcommunity.auth.interfaces;

import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public abstract class AuthUtil {

    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    /**
     * 웹 응답에 리프레시 토큰 쿠키를 추가합니다.
     * @param response     웹 응답
     * @param refreshToken 리프레시 토큰
     * @param expires      토큰 만료일
     */
    public static void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, Date expires) {
        int refreshTokenExpirationTime = (int) ((expires.getTime() - new Date().getTime()) / 1000);

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(refreshTokenExpirationTime);
        refreshTokenCookie.setPath("/");

        response.addCookie(refreshTokenCookie);
    }

    /**
     * 웹 응답에 리프레시 토큰 쿠키 제거용 쿠키를 추가합니다.
     * @param response 웹 응답
     */
    public static void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");

        response.addCookie(refreshTokenCookie);
    }
}
