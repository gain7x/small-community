package com.practice.smallcommunity.security.user;

import java.util.Map;

public abstract class AbstractSocialUser implements SocialUser {

    private final Map<String, Object> attributes;

    public AbstractSocialUser(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}
