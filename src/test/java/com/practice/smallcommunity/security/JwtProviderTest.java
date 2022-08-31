package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.utils.DomainGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    JwtProvider jwtProvider = new JwtProvider();

    @Spy
    Member member = DomainGenerator.createMember("A");

    @BeforeEach
    void setUp() {
        jwtProvider.init();
    }

    @Test
    void 액세스_토큰_발행() {
        //given
        when(member.getId()).thenReturn(1L);

        //when
        String token = jwtProvider.createAccessToken(member);

        //then
        assertThat(token).isNotNull();
    }

    @Test
    void 액세스_토큰이_유효하면_인증객체를_반환한다() {
        //given
        when(member.getId()).thenReturn(1L);
        String token = jwtProvider.createAccessToken(member);

        //when
        Authentication authentication = jwtProvider.getAuthentication(token);

        //then
        assertThat(authentication).isNotNull();
    }

    @Test
    void 토큰이_서명이_검증되면_주체를_반환한다() {
        //given
        when(member.getId()).thenReturn(1L);
        String token = jwtProvider.createAccessToken(member);

        //when
        //then
        assertThatNoException().isThrownBy(() -> jwtProvider.getSubject(token));
    }

    @Test
    void 토큰이_유효하면_참을_반환한다() {
        //given
        when(member.getId()).thenReturn(1L);
        String token = jwtProvider.createAccessToken(member);

        //when
        boolean result = jwtProvider.isValid(token);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 리프레시_토큰_발행() {
        String refreshToken = jwtProvider.createRefreshToken();
        boolean valid = jwtProvider.isValid(refreshToken);

        assertThat(valid).isTrue();
    }
}