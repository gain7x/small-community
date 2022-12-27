package com.practice.smallcommunity.security;

import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.OAUTH2_AUTHORIZATION_COOKIE;
import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.OAUTH2_REDIRECT_URI_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void 미지원_삭제_메서드를_호출하면_예외를_던진다() {
        //when
        //then
        assertThatThrownBy(() -> authorizationRequestRepository.removeAuthorizationRequest(request))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 인가_요청_저장_시_인가_요청_객체가_NULL이고_이미_저장된_인가_요청이_존재하면_삭제한다() {
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
        authorizationRequestRepository.saveAuthorizationRequest(null, request, response);

        //then
        // 앞선 인가 요청 저장 작업으로 인해 동일한 이름의 쿠키가 2개 존재하는 상태입니다( 저장용/삭제용 ).
        // 맨 마지막 쿠키가 삭제를 의미하는 쿠키이므로 이를 검증합니다.
        Cookie lastAuthorizationCookie = null;
        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(OAUTH2_AUTHORIZATION_COOKIE)) {
                lastAuthorizationCookie = cookie;
            }
        }
        assertThat(lastAuthorizationCookie).isNotNull();
        assertThat(lastAuthorizationCookie.getValue()).isNull();
        assertThat(lastAuthorizationCookie.getMaxAge()).isZero();
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
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("The redirect_uri parameter is required");
    }

    @Test
    void 인가_요청_저장_시_리다이렉트_URI가_인가되지_않은_도메인이면_예외를_던진다() {
        //given
        request.setParameter(CookieOAuth2AuthorizationRequestRepository.OAUTH2_REDIRECT_URI_PARAM, "https://other-client.com");
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
            .clientId("test")
            .authorizationUri("https://test.com/authorization")
            .build();

        //when
        assertThatThrownBy(
            () -> authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("redirect_uri uses an unauthorized domain");
    }

    @Test
    void 저장된_인가_요청을_조회한다() {
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
    void 저장된_인가_요청_조회_시_리다이렉트_URI가_쿠키에_없으면_null을_반환한다() {
        //when
        request.setCookies(new Cookie("test", "test"));
        OAuth2AuthorizationRequest result = authorizationRequestRepository.loadAuthorizationRequest(request);

        //then
        assertThat(result).isNull();
    }

    @Test
    void 저장된_인가_요청_조회_시_state_파라미터가_없으면_null을_반환한다() {
        //given
        request.setParameter(CookieOAuth2AuthorizationRequestRepository.OAUTH2_REDIRECT_URI_PARAM, "https://client.com");
        request.setCookies(new Cookie(OAUTH2_REDIRECT_URI_COOKIE, "https://client.com"));

        //when
        OAuth2AuthorizationRequest result = authorizationRequestRepository.loadAuthorizationRequest(request);

        //then
        assertThat(result).isNull();
    }

    @Test
    void 저장된_인가_요청_조회_시_쿠키에_인가_요청_데이터가_없으면_null을_반환한다() {
        //given
        request.setParameter(CookieOAuth2AuthorizationRequestRepository.OAUTH2_REDIRECT_URI_PARAM, "https://client.com");
        request.setCookies(new Cookie(OAUTH2_REDIRECT_URI_COOKIE, "https://client.com"));
        request.setParameter("state", "state");

        //when
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