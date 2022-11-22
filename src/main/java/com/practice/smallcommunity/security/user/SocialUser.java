package com.practice.smallcommunity.security.user;

import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;

public interface SocialUser {

    String getUsername();

    String getEmail();

    OAuth2Platform getPlatform();
}