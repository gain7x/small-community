package com.practice.smallcommunity.security.converter;

import com.practice.smallcommunity.member.domain.OAuth2Platform;
import com.practice.smallcommunity.security.user.GoogleUser;
import com.practice.smallcommunity.security.user.SocialUser;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class GoogleUserConverter implements SocialUserConverter {

    @Override
    public SocialUser convert(OAuth2AuthenticationToken authenticationToken) {
        return authenticationToken.getAuthorizedClientRegistrationId().equals(OAuth2Platform.GOOGLE.getRegistrationId())
            ? new GoogleUser(authenticationToken.getPrincipal()) : null;
    }
}
