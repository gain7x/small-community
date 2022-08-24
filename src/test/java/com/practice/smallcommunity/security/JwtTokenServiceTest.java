package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class JwtTokenServiceTest {

    JwtTokenService jwtTokenService = new JwtTokenService();

    Member member = Member.builder()
        .id(1L)
        .email("userA@email.com")
        .password("pass")
        .memberRole(MemberRole.ROLE_USER)
        .build();

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