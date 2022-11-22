package com.practice.smallcommunity.security;

import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.OAUTH2_AUTHORIZATION_COOKIE;
import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.OAUTH2_REDIRECT_URI_COOKIE;

import com.practice.smallcommunity.utils.CookieUtil;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;

/**
 * OAuth2 인가 요청을 쿠키로 관리하는 저장소입니다.
 */
@RequiredArgsConstructor
public class CookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String OAUTH2_REDIRECT_URI_PARAM = "redirect_uri";
    private static final int COOKIE_EXPIRE_SECONDS = 180;

    private final List<String> authorizedDomains;

    /**
     * 웹 요청 쿠키에 유효한 인가 요청 정보가 존재하면 인가 요청 객체를 반환합니다.
     *  리다이렉트 URI 유효성, state 파라미터 유효성 등을 검사합니다.
     * @param request the {@code HttpServletRequest}
     * @return 유효한 인가 요청 객체 존재 시 인가 요청 객체 반환, 없으면 null 반환
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        if (CookieUtil.getCookie(request, OAUTH2_REDIRECT_URI_COOKIE) == null) {
            return null;
        }
        return getAuthorizationRequest(request);
    }

    /**
     * OAuth2 인가 요청 및 리다이렉트 URI를 저장합니다.
     * @param authorizationRequest the {@link OAuth2AuthorizationRequest}
     * @param request the {@code HttpServletRequest}
     * @param response the {@code HttpServletResponse}
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
        HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
        } else {
            saveRedirectUriCookie(request, response);
            CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_COOKIE, serializeToBase64(authorizationRequest),
                COOKIE_EXPIRE_SECONDS);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        throw new IllegalStateException("This is a deprecated function");
    }

    /**
     * 쿠키에서 OAuth2 인가 요청 정보를 제거합니다.
     * 리다이렉트 URI는 이 위치에서 제거하지 않으며, 성공/실패 핸들러( @see )에서 제거합니다.
     * @param request the {@code HttpServletRequest}
     * @param response the {@code HttpServletResponse}
     * @see OAuth2AuthenticationSuccessHandler
     * @see OAuth2AuthenticationFailureHandler
     * @return 제거된 인가 요청 객체
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
        HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = getAuthorizationRequest(request);
        if (authorizationRequest != null) {
            CookieUtil.deleteCookie(response, OAUTH2_AUTHORIZATION_COOKIE);
        }

        return authorizationRequest;
    }

    private String getRedirectUriParameter(HttpServletRequest request) {
        return request.getParameter(OAUTH2_REDIRECT_URI_PARAM);
    }

    private void saveRedirectUriCookie(HttpServletRequest request, HttpServletResponse response) {
        String redirectUri = getRedirectUriParameter(request);
        if (redirectUri == null) {
            throw new IllegalArgumentException("The redirect_uri parameter is required");
        }
        boolean isAuthorizedDomain = authorizedDomains.stream().anyMatch(redirectUri::startsWith);
        if (!isAuthorizedDomain) {
            throw new IllegalArgumentException("redirect_uri uses an unauthorized domain");
        }
        CookieUtil.addCookie(response, OAUTH2_REDIRECT_URI_COOKIE, redirectUri, COOKIE_EXPIRE_SECONDS);
    }

    private OAuth2AuthorizationRequest getAuthorizationRequest(HttpServletRequest request) {
        String stateParameter = this.getStateParameter(request);
        if (stateParameter == null) {
            return null;
        }

        Cookie authorizationRequestCookie = CookieUtil.getCookie(request, OAUTH2_AUTHORIZATION_COOKIE);
        if (authorizationRequestCookie == null) {
            return null;
        }

        OAuth2AuthorizationRequest authorizationRequest = deserializeFromBase64(authorizationRequestCookie.getValue());

        return authorizationRequest.getState().equals(stateParameter) ? authorizationRequest : null;
    }

    private String getStateParameter(HttpServletRequest request) {
        return request.getParameter("state");
    }

    private String serializeToBase64(OAuth2AuthorizationRequest authorizationRequest) {
        byte[] serializedRequest = SerializationUtils.serialize(authorizationRequest);
        if (serializedRequest == null) {
            throw new NullPointerException("Authorization request cannot be null");
        }

        return Base64Utils.encodeToString(serializedRequest);
    }

    private OAuth2AuthorizationRequest deserializeFromBase64(String base64AuthorizationRequest) {
        return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(
            Base64Utils.decodeFromString(base64AuthorizationRequest));
    }
}
