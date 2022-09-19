package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.security.dto.TokenDto;
import com.practice.smallcommunity.utils.DomainGenerator;
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
    void 토큰_서명이_검증되면_주체를_반환한다() {
        //given
        when(dummyMember.getId()).thenReturn(1L);
        TokenDto token = jwtProvider.createAccessToken(dummyMember);

        //when
        //then
        assertThatNoException().isThrownBy(() -> jwtProvider.getSubject(token.getToken()));
    }

    @Test
    void 토큰이_유효하면_참을_반환한다() {
        //given
        when(dummyMember.getId()).thenReturn(1L);
        TokenDto token = jwtProvider.createAccessToken(dummyMember);

        //when
        boolean result = jwtProvider.isValid(token.getToken());

        //then
        assertThat(result).isTrue();
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