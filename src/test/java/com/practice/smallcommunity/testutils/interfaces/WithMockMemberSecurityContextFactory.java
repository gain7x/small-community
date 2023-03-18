package com.practice.smallcommunity.testutils.interfaces;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockMemberSecurityContextFactory implements WithSecurityContextFactory<WithMockMember> {

    @Override
    public SecurityContext createSecurityContext(WithMockMember withMockMember) {
        long principal = withMockMember.memberId();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : withMockMember.roles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
