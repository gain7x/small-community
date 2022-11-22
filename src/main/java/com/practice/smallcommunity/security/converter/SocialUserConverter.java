package com.practice.smallcommunity.security.converter;

import com.practice.smallcommunity.security.user.SocialUser;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface SocialUserConverter {

    SocialUser convert(OAuth2AuthenticationToken authenticationToken);
}
