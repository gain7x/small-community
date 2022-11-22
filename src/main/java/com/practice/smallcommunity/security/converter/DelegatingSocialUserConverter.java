package com.practice.smallcommunity.security.converter;

import com.practice.smallcommunity.security.user.SocialUser;
import java.util.List;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class DelegatingSocialUserConverter implements SocialUserConverter {

    private final List<SocialUserConverter> converters;

    public DelegatingSocialUserConverter() {
        converters = List.of(new GoogleUserConverter(), new NaverUserConverter());
    }

    @Override
    public SocialUser convert(OAuth2AuthenticationToken authenticationToken) {
        for (SocialUserConverter converter : converters) {
            SocialUser socialUser = converter.convert(authenticationToken);
            if (socialUser != null) {
                return socialUser;
            }
        }

        return null;
    }
}
