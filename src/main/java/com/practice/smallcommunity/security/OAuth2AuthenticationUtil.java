package com.practice.smallcommunity.security;

import com.practice.smallcommunity.utils.CookieUtil;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class OAuth2AuthenticationUtil {
    public static final String OAUTH2_AUTHORIZATION_COOKIE = "oauth2_authorization_request";
    public static final String OAUTH2_REDIRECT_URI_COOKIE = "oauth2_redirect_uri";

    /**
     * 웹 요청 쿠키에서 리다이렉트 URI를 검색합니다.
     * @param request 웹 요청
     * @return 존재하면 null이 아닌 값을 반환합니다.
     */
    public static String getRedirectUri(HttpServletRequest request) {
        Cookie redirectUriCookie = CookieUtil.getCookie(request, OAUTH2_REDIRECT_URI_COOKIE);
        if (redirectUriCookie == null) {
            return null;
        }
        return redirectUriCookie.getValue();
    }

    /**
     * OAuth2 인증용으로 사용했던 임시 정보들을 삭제합니다.
     * @param response 웹 응답
     */
    public static void clearAuthenticationAttributes(HttpServletResponse response) {
        CookieUtil.deleteCookie(response, OAUTH2_REDIRECT_URI_COOKIE);
    }
}
