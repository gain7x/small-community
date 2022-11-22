package com.practice.smallcommunity.security.user;

import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class NaverUser extends AbstractSocialUser {

    public NaverUser(OAuth2User oAuth2User) {
        super((Map<String, Object>) oAuth2User.getAttributes().get("response"));
    }

    @Override
    public String getUsername() {
        return (String) getAttributes().get("id");
    }

    @Override
    public String getEmail() {
        return (String) getAttributes().get("email");
    }

    @Override
    public OAuth2Platform getPlatform() {
        return OAuth2Platform.NAVER;
    }
}
