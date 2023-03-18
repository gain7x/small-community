package com.practice.smallcommunity.security.user;

import com.practice.smallcommunity.member.domain.OAuth2Platform;

public interface SocialUser {

    String getUsername();

    String getEmail();

    OAuth2Platform getPlatform();
}