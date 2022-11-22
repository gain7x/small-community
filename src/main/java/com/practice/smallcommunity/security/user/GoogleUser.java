package com.practice.smallcommunity.security.user;

import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleUser extends AbstractSocialUser {

    public GoogleUser(OAuth2User oAuth2User) {
        super(oAuth2User.getAttributes());
    }

    @Override
    public String getUsername() {
        return (String) getAttributes().get("sub");
    }

    @Override
    public String getEmail() {
        return (String) getAttributes().get("email");
    }

    @Override
    public OAuth2Platform getPlatform() {
        return OAuth2Platform.GOOGLE;
    }
}
