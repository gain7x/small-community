package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.security.dto.TokenDto;
import com.practice.smallcommunity.utils.DomainGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
    Member dummyMember = DomainGenerator.createMember("A");

    @BeforeEach
    void setUp() {
        jwtProvider.init();
    }

    private String createExpiredToken() {
        Date expires = Date.from(
            Instant.now().plus(-1, ChronoUnit.MINUTES));

        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, jwtProvider.getKey())
            .setIssuedAt(new Date())
            .setSubject(Long.toString(1L))
            .setExpiration(expires)
            .compact();
    }

    private String createInvalidToken() {
        Date expires = Date.from(
            Instant.now().plus(10, ChronoUnit.MINUTES));

        byte[] invalidKey = "invalid".getBytes();

        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, invalidKey)
            .setIssuedAt(new Date())
            .setSubject(Long.toString(1L))
            .setExpiration(expires)
            .compact();
    }

    @Test
    void 서명에_사용중인_키를_반환한다() {
        //when
        byte[] key = jwtProvider.getKey();

        //then
        assertThat(key).isNotNull();
        assertThat(key.length).isNotZero();
    }

    @Test
    void 새로고침_토큰_만료_시간을_반환한다() {
        //when
        long result = jwtProvider.getRefreshTokenExpirationHours();

        //then
        assertThat(result).isEqualTo(24);
    }

    @Test
    void 액세스_토큰_발행() {
        //given
        when(dummyMember.getId()).thenReturn(1L);

        //when
        TokenDto token = jwtProvider.createAccessToken(dummyMember);

        //then
        assertThat(token).isNotNull();
    }

    @Test
    void 액세스_토큰이_유효하면_인증객체를_반환한다() {
        //given
        when(dummyMember.getId()).thenReturn(1L);
        TokenDto token = jwtProvider.createAccessToken(dummyMember);

        //when
        Authentication authentication = jwtProvider.getAuthentication(token.getToken());

        //then
        assertThat(authentication).isNotNull();
    }

    @Test
    void 액세스_토큰이_유효하지_않으면_null을_반환한다() {
        //given
        String token = createInvalidToken();

        //when
        Authentication authentication = jwtProvider.getAuthentication(token);

        //then
        assertThat(authentication).isNull();
    }

    @Test
    void 토큰_서명이_검증되면_주체를_반환한다() {
        //given
        when(dummyMember.getId()).thenReturn(1L);
        TokenDto token = jwtProvider.createAccessToken(dummyMember);

        //when
        //then
        assertThatNoException().isThrownBy(() -> jwtProvider.getSubject(token.getToken()));
    }

    @Test
    void 토큰_주체_조회_시_토큰이_만료_상태여도_주체를_반환한다() {
        //given
        String token = createExpiredToken();

        //when
        //then
        assertThatNoException().isThrownBy(() -> jwtProvider.getSubject(token));
    }

    @Test
    void 토큰_주체_조회_시_토큰이_유효하지_않으면_예외를_던진다() {
        //given
        String token = createInvalidToken();

        //when
        //then
        assertThatThrownBy(() -> jwtProvider.getSubject(token))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 토큰이_유효하면_true를_반환한다() {
        //given
        when(dummyMember.getId()).thenReturn(1L);
        TokenDto token = jwtProvider.createAccessToken(dummyMember);

        //when
        boolean result = jwtProvider.isValid(token.getToken());

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 토큰이_유효하지_않으면_false를_반환한다() {
        //given
        String token = createInvalidToken();

        //when
        boolean result = jwtProvider.isValid(token);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void 리프레시_토큰_발행() {
        //given
        when(dummyMember.getId()).thenReturn(1L);

        //when
        TokenDto refreshToken = jwtProvider.createRefreshToken(dummyMember);

        //then
        assertThat(refreshToken).isNotNull();
    }
}