package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.auth.OAuth2RegistrationTokenService;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import com.practice.smallcommunity.security.exception.NotRegisteredOAuth2LoginException;
import com.practice.smallcommunity.security.user.SocialUser;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationFailureHandlerTest {

    @Mock
    OAuth2RegistrationTokenService oauth2RegistrationTokenService;

    OAuth2AuthenticationFailureHandler authenticationFailureHandler;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    @BeforeEach
    void setUp() {
        authenticationFailureHandler = new OAuth2AuthenticationFailureHandler(oauth2RegistrationTokenService);
    }

    @Test
    void 던져진_인증_예외가_미가입_예외이면_가입용_토큰을_저장하고_키를_리다이렉트한다() throws ServletException, IOException {
        //given
        SocialUser socialUser = mock(SocialUser.class);
        when(socialUser.getEmail()).thenReturn("test@mail.com");
        when(socialUser.getUsername()).thenReturn("test");
        when(socialUser.getPlatform()).thenReturn(OAuth2Platform.GOOGLE);

        when(oauth2RegistrationTokenService.createRegistrationToken(
            eq("test@mail.com"), eq("test"), eq(OAuth2Platform.GOOGLE))).thenReturn("registration_token");

        //when
        authenticationFailureHandler.onAuthenticationFailure(request, response,
            new NotRegisteredOAuth2LoginException(null, socialUser, "https://test.com"));

        //then
        verify(oauth2RegistrationTokenService, times(1)).createRegistrationToken(
           eq("test@mail.com"), eq("test"), eq(OAuth2Platform.GOOGLE));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value());
        assertThat(response.getRedirectedUrl()).containsPattern("[&?]response_type=REQUIRED_REGISTRATION[&?]key=");
    }
}