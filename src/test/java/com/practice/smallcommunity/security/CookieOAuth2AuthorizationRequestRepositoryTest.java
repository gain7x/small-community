package com.practice.smallcommunity.security;

import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.OAUTH2_AUTHORIZATION_COOKIE;
import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.OAUTH2_REDIRECT_URI_COOKIE;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

class CookieOAuth2AuthorizationRequestRepositoryTest {

    CookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    @BeforeEach
    void setUp() {
        authorizationRequestRepository = new CookieOAuth2AuthorizationRequestRepository(List.of("https://client.com"));
    }

    @Test
    void 인가_요청을_저장한다() {
        //given
        request.setParameter(CookieOAuth2AuthorizationRequestRepository.OAUTH2_REDIRECT_URI_PARAM, "https://client.com");
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
            .clientId("test")
            .authorizationUri("https://test.com/authorization")
            .build();

        //when
        authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);

        //then
        assertThat(response.getCookie(OAUTH2_AUTHORIZATION_COOKIE)).isNotNull();
        assertThat(response.getCookie(OAUTH2_REDIRECT_URI_COOKIE)).isNotNull();
    }

    @Test
    void 인가_요청_저장_시_리다이렉트_URI가_없으면_예외를_던진다() {
        //given
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
            .clientId("test")
            .authorizationUri("https://test.com/authorization")
            .build();

        //when
        assertThatThrownBy(
            () -> authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 인가_요청을_조회한다() {
        //given
        request.setParameter(CookieOAuth2AuthorizationRequestRepository.OAUTH2_REDIRECT_URI_PARAM, "https://client.com");
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
            .clientId("test")
            .authorizationUri("https://test.com/authorization")
            .state("test-state")
            .build();

        authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);

        request.setCookies(response.getCookies());
        request.setParameter("state", authorizationRequest.getState());

        //when
        OAuth2AuthorizationRequest result = authorizationRequestRepository.loadAuthorizationRequest(request);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void 인가_요청_조회_시_리다이렉트_URI가_쿠키에_없으면_null을_반환한다() {
        //when
        request.setCookies(new Cookie("test", "test"));
        OAuth2AuthorizationRequest result = authorizationRequestRepository.loadAuthorizationRequest(request);

        //then
        assertThat(result).isNull();
    }

    @Test
    void 인가_요청을_조회하며_제거한다() {
        //given
        request.setParameter(CookieOAuth2AuthorizationRequestRepository.OAUTH2_REDIRECT_URI_PARAM, "https://client.com");
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
            .clientId("test")
            .authorizationUri("https://test.com/authorization")
            .state("test-state")
            .build();

        authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);

        request.setCookies(response.getCookies());
        request.setParameter("state", authorizationRequest.getState());

        //when
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        OAuth2AuthorizationRequest result = authorizationRequestRepository.removeAuthorizationRequest(request, response2);
        Cookie authorizationCookie = response2.getCookie(OAUTH2_AUTHORIZATION_COOKIE);

        //then
        assertThat(result).isNotNull();
        assertThat(authorizationCookie).isNotNull();
        assertThat(authorizationCookie.getMaxAge()).isEqualTo(0);
    }
}