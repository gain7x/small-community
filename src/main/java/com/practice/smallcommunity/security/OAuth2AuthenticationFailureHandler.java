package com.practice.smallcommunity.security;

import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.clearAuthenticationAttributes;

import com.practice.smallcommunity.auth.application.OAuth2RegistrationTokenService;
import com.practice.smallcommunity.member.interfaces.OAuth2RegisterController;
import com.practice.smallcommunity.security.exception.NotRegisteredOAuth2LoginException;
import com.practice.smallcommunity.security.user.SocialUser;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * OAuth2 로그인 실패 시 호출되는 핸들러입니다.
 */
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final OAuth2RegistrationTokenService oauth2RegistrationTokenService;

    /**
     * 인증 예외가 미가입 예외인 경우 OAuth2 사용자의 회원가입용 토큰을 저장하고, 클라이언트가 지정한 URI로 토큰 접근 정보를 리다이렉트합니다.
     * @see OAuth2AuthenticationSuccessHandler
     * @see OAuth2RegisterController
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {

        clearAuthenticationAttributes(response);

        if (exception instanceof NotRegisteredOAuth2LoginException) {
            NotRegisteredOAuth2LoginException oauth2LoginException = (NotRegisteredOAuth2LoginException) exception;
            SocialUser socialUser = oauth2LoginException.getSocialUser();
            String key = oauth2RegistrationTokenService.createRegistrationToken(socialUser.getEmail(),
                socialUser.getUsername(), socialUser.getPlatform());

            redirectToClient(response, oauth2LoginException.getRedirectUri(), socialUser.getEmail(), key);
        }
    }

    private void redirectToClient(HttpServletResponse response, String targetUrl, String email, String key) throws IOException {
        String uri = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("response_type", OAuth2ResponseType.REQUIRED_REGISTRATION.name())
            .queryParam("email", email)
            .queryParam("key", key)
            .build()
            .toUriString();

        response.sendRedirect(uri);
    }
}
