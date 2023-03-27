package com.practice.smallcommunity.testutils;

import com.practice.smallcommunity.member.domain.MemberRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class TestSecurityUtil {

    public static Authentication createAuthentication(String role) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return new UsernamePasswordAuthenticationToken(1L, null, authorities);
    }

    public static void setUserAuthentication() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(MemberRole.USER.getAuthority()));
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(1L, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public static void setAdminAuthentication() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(MemberRole.ADMIN.getAuthority()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
