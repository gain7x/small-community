package com.practice.smallcommunity.security;

import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.OAUTH2_REDIRECT_URI_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.auth.application.AuthService;
import com.practice.smallcommunity.auth.application.OAuth2LoginService;
import com.practice.smallcommunity.auth.application.dto.AuthDto;
import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.auth.domain.OAuth2Login;
import com.practice.smallcommunity.member.domain.OAuth2Platform;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.auth.interfaces.AuthUtil;
import com.practice.smallcommunity.security.exception.NotRegisteredOAuth2LoginException;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    OAuth2LoginService oauth2LoginService;

    @Mock
    AuthService authService;

    OAuth2AuthenticationSuccessHandler authenticationSuccessHandler;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    OAuth2User oauth2User = mock(OAuth2User.class);

    @BeforeEach
    void setUp() {
        authenticationSuccessHandler = new OAuth2AuthenticationSuccessHandler(oauth2LoginService, authService);
    }

    @Test
    void OAuth2_인증정보가_소셜_사용자_객체로_변환되지_않으면_예외를_던진다() throws ServletException, IOException {
        //given
        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(oauth2User, null, "test");

        request.setCookies(new Cookie(OAUTH2_REDIRECT_URI_COOKIE, "https://test.com"));

        //when
        //then
        assertThatThrownBy(
            () -> authenticationSuccessHandler.onAuthenticationSuccess(request, response, authenticationToken))
            .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    void OAuth2_요청_쿠키에_리다이렉트_URI가_없으면_예외를_던진다() throws ServletException, IOException {
        //given
        when(oauth2User.getAttributes()).thenReturn(Map.of("sub", "testUsername"));
        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(oauth2User, null,
            OAuth2Platform.GOOGLE.getRegistrationId());

        request.setCookies(new Cookie("test", "test"));

        //when
        //then
        assertThatThrownBy(
            () -> authenticationSuccessHandler.onAuthenticationSuccess(request, response, authenticationToken))
            .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    void OAuth2_로그인_사용자가_회원이_아니면_미가입_예외를_던진다() {
        //given
        when(oauth2User.getAttributes()).thenReturn(Map.of("sub", "testUsername"));
        when(oauth2LoginService.findOne("testUsername", OAuth2Platform.GOOGLE))
            .thenThrow(new BusinessException(ErrorCode.NOT_FOUND_OAUTH2_LOGIN));

        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(oauth2User, null,
            OAuth2Platform.GOOGLE.getRegistrationId());

        request.setCookies(new Cookie(OAUTH2_REDIRECT_URI_COOKIE, "https://test.com"));

        //when
        //then
        assertThatThrownBy(
            () -> authenticationSuccessHandler.onAuthenticationSuccess(request, response, authenticationToken))
            .isInstanceOf(NotRegisteredOAuth2LoginException.class);
    }

    @Test
    void OAuth2_로그인_사용자가_회원이면_지정한_URI로_리프레시_토큰을_리다이렉트한다() throws ServletException, IOException {
        //given
        when(oauth2User.getAttributes()).thenReturn(Map.of("sub", "testUsername"));

        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(oauth2User, null,
            OAuth2Platform.GOOGLE.getRegistrationId());

        Member member = DomainGenerator.createMember("A");
        OAuth2Login oauth2Login = DomainGenerator.createOAuth2Login(member, "testUsername", OAuth2Platform.GOOGLE);
        when(oauth2LoginService.findOne("testUsername", OAuth2Platform.GOOGLE)).thenReturn(oauth2Login);

        AuthDto authDto = AuthDto.builder()
            .member(member)
            .accessToken("access-token")
            .accessTokenExpires(Date.from(Instant.now().plus(1, ChronoUnit.MINUTES)))
            .refreshToken("refresh-token")
            .refreshTokenExpires(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .build();
        when(authService.createAuthentication(member)).thenReturn(authDto);

        request.setCookies(new Cookie(OAUTH2_REDIRECT_URI_COOKIE, "https://test.com"));

        //when
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authenticationToken);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value());
        assertThat(response.getCookie(AuthUtil.REFRESH_TOKEN_COOKIE)).isNotNull();
        assertThat(response.getCookie(AuthUtil.REFRESH_TOKEN_COOKIE).getValue()).isNotBlank();
        assertThat(response.getRedirectedUrl()).containsPattern("[&?]response_type=AUTHENTICATED");
    }
}