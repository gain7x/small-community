package com.practice.smallcommunity.security;

import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.security.dto.TokenDto;
import com.practice.smallcommunity.testutils.DomainGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    JwtProvider jwtProvider = new JwtProvider(new JwtProperties("secretKey", 30, 336));

    @Spy
    Member dummyMember = DomainGenerator.createMember("A");

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
        Authentication authentication = jwtProvider.authenticate(token.getToken());

        //then
        assertThat(authentication).isNotNull();
    }

    @Test
    void 액세스_토큰이_유효하지_않으면_null을_반환한다() {
        //given
        String token = createInvalidToken();

        //when
        Authentication authentication = jwtProvider.authenticate(token);

        //then
        assertThat(authentication).isNull();
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