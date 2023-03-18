package com.practice.smallcommunity.security;

import static com.practice.smallcommunity.auth.interfaces.AuthUtil.setRefreshTokenCookie;
import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.clearAuthenticationAttributes;
import static com.practice.smallcommunity.security.OAuth2AuthenticationUtil.getRedirectUri;

import com.practice.smallcommunity.auth.application.AuthService;
import com.practice.smallcommunity.auth.application.OAuth2LoginService;
import com.practice.smallcommunity.auth.application.dto.AuthDto;
import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.auth.domain.oauth2.OAuth2Login;
import com.practice.smallcommunity.security.converter.DelegatingSocialUserConverter;
import com.practice.smallcommunity.security.converter.SocialUserConverter;
import com.practice.smallcommunity.security.exception.NotRegisteredOAuth2LoginException;
import com.practice.smallcommunity.security.user.SocialUser;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * OAuth2 로그인 성공 시 호출되는 핸들러입니다.
 */
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SocialUserConverter converter = new DelegatingSocialUserConverter();
    private final OAuth2LoginService oauth2LoginService;
    private final AuthService authService;

    /**
     * OAuth2 로그인 사용자가 회원이면 클라이언트에서 지정한 URI로 리다이렉트하고, 비회원이면 미가입 예외를 던집니다.
     *  리다이렉트 시 쿠키에 리프레시 토큰이 추가됩니다.
     * @see OAuth2AuthenticationFailureHandler
     *      미가입 예외가 던져지면 이를 처리하는 핸들러입니다.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        SocialUser socialUser = converter.convert(authenticationToken);
        String redirectUri = getRedirectUri(request);
        if (socialUser == null || redirectUri == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT));
        }

        try {
            OAuth2Login oAuth2Login = oauth2LoginService.findOne(socialUser.getUsername(), socialUser.getPlatform());
            AuthDto authDto = authService.createAuthentication(oAuth2Login.getMember());

            clearAuthenticationAttributes(response);
            setRefreshTokenCookie(response, authDto.getRefreshToken(), authDto.getRefreshTokenExpires());
            redirectToClient(response, redirectUri);
        } catch (BusinessException e) {
            throw new NotRegisteredOAuth2LoginException(null, socialUser, redirectUri);
        }
    }

    private void redirectToClient(HttpServletResponse response, String targetUrl) throws IOException {
        String uri = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("response_type", OAuth2ResponseType.AUTHENTICATED.name())
            .build()
            .toUriString();

        response.sendRedirect(uri);
    }
}
