package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.member.Member;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    JwtTokenService jwtTokenService;

    JwtAuthenticationFilter jwtAuthenticationFilter;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    Member member = Member.builder()
        .id(1L)
        .email("userA@mail.com")
        .password("pass")
        .build();

    @BeforeEach
    void beforeEach() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenService);
    }

    @Test
    void 토큰이_있으면_인증객체를_등록한다() throws ServletException, IOException {
        when(jwtTokenService.createToken(member)).thenReturn("jwt-token");
        when(jwtTokenService.getAuthentication("jwt-token"))
            .thenReturn(new UsernamePasswordAuthenticationToken("userA", null, null));

        String token = jwtTokenService.createToken(member);
        request.addHeader("Authorization", "Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
    }

    @Test
    void 토큰이_없으면_인증객체는_NULL() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNull();
    }
}