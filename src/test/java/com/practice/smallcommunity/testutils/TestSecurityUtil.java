package com.practice.smallcommunity.testutils;

import com.practice.smallcommunity.domain.member.MemberRole;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class TestSecurityUtil {

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
