package com.practice.smallcommunity.utils;

import com.practice.smallcommunity.member.domain.MemberRole;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SecurityUtil {

    public static boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return !(authentication instanceof AnonymousAuthenticationToken);
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals(MemberRole.ADMIN.getAuthority()));
    }
}
