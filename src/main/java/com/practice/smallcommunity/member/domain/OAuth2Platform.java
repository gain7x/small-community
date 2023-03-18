package com.practice.smallcommunity.member.domain;

public enum OAuth2Platform {
    GOOGLE("google"),
    GITHUB("github"),
    NAVER("naver");

    private final String registrationId;

    OAuth2Platform(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }
}
