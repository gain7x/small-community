package com.practice.smallcommunity.security.exception;

import com.practice.smallcommunity.security.user.SocialUser;
import org.springframework.security.core.AuthenticationException;

public class NotRegisteredOAuth2LoginException extends AuthenticationException {

    private final SocialUser socialUser;
    private final String redirectUri;

    public NotRegisteredOAuth2LoginException(String msg, Throwable cause, SocialUser socialUser, String redirectUri) {
        super(msg, cause);
        this.socialUser = socialUser;
        this.redirectUri = redirectUri;
    }

    public NotRegisteredOAuth2LoginException(String msg, SocialUser socialUser, String redirectUri) {
        super(msg);
        this.socialUser = socialUser;
        this.redirectUri = redirectUri;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
