package com.practice.smallcommunity.domain.member;

public enum MemberRole {
    USER,
    ADMIN;

    private final String roleName;

    MemberRole() {
        roleName = "ROLE_" + this.name();
    }

    public String getAuthority() {
        return roleName;
    }
}