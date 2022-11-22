package com.practice.smallcommunity.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CookieUtil {

    /**
     * 웹 요청에 이름이 일치하는 쿠키가 있는지 검색합니다.
     * @param request    웹 요청
     * @param cookieName 쿠키 이름
     * @return 쿠키가 있는 경우 null이 아닌 값을 반환
     */
    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }

        return null;
    }

    /**
     * 웹 응답에 쿠키를 추가합니다.
     * @param response    웹 응답
     * @param cookieName  쿠키 이름
     * @param cookieValue 쿠키값
     * @param expiry      쿠키 유효 시간( 초 )
     */
    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int expiry) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiry);

        response.addCookie(cookie);
    }

    /**
     * 웹 응답에 쿠키 제거용 쿠키를 추가합니다.
     *  HttpServletResponse에서 sendRedirect()와 같은 함수를 실행하기 전에 호출해야 합니다.
     * @param response   웹 응답.
     * @param cookieName 쿠키 이름
     * @see org.apache.catalina.connector.ResponseFacade addCookie(), sendRedirect() 메서드 참고
     */
    public static void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
