package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    JwtTokenProvider jwtTokenService = new JwtTokenProvider();

    @Spy
    Member member = DomainGenerator.createMember("A");

    @BeforeEach
    void setUp() {
        when(member.getId()).thenReturn(1L);
    }

    @Test
    void 토큰발행() {
        //when
        String token = jwtTokenService.createToken(member);

        //then
        assertThat(token).isNotNull();
    }

    @Test
    void 토큰검증() {
        //given
        String token = jwtTokenService.createToken(member);

        //when
        Authentication authentication = jwtTokenService.getAuthentication(token);

        //then
        assertThat(authentication).isNotNull();
    }
}