package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.security.dto.TokenDto;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
    JwtProvider jwtProvider;

    JwtAuthenticationFilter jwtAuthenticationFilter;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    Member dummyMember = DomainGenerator.createMember("A");

    TokenDto dummyAccessToken = TokenDto.builder()
        .token("some-access-token")
        .expires(Date.from(Instant.now().plus(1, ChronoUnit.MINUTES)))
        .build();

    @BeforeEach
    void beforeEach() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    void 토큰이_있으면_인증객체를_등록한다() throws ServletException, IOException {
        when(jwtProvider.createAccessToken(dummyMember)).thenReturn(dummyAccessToken);
        when(jwtProvider.authenticate(dummyAccessToken.getToken()))
            .thenReturn(new UsernamePasswordAuthenticationToken("userA", null, null));

        TokenDto token = jwtProvider.createAccessToken(dummyMember);
        request.addHeader("Authorization", "Bearer " + token.getToken());

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