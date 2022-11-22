package com.practice.smallcommunity.security.converter;

import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import com.practice.smallcommunity.security.user.NaverUser;
import com.practice.smallcommunity.security.user.SocialUser;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class NaverUserConverter implements SocialUserConverter {

    @Override
    public SocialUser convert(OAuth2AuthenticationToken authenticationToken) {
        return authenticationToken.getAuthorizedClientRegistrationId().equals(OAuth2Platform.NAVER.getRegistrationId())
            ? new NaverUser(authenticationToken.getPrincipal()) : null;
    }
}
