package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.Role;
import com.practice.smallcommunity.domain.member.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class JwtTokenServiceTest {

    JwtTokenService jwtTokenService = new JwtTokenService();

    Member member = Member.builder()
        .id(1L)
        .username("userA")
        .password("pass")
        .email("userA@email.com")
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

    @Test
    void 토큰에_권한정보_추가() {
        //given
        Role role = Role.builder()
            .roleType(RoleType.ROLE_USER)
            .build();

        member.addRole(role);

        //when
        String token = jwtTokenService.createToken(member);

        //then
        assertThat(token).isNotNull();
    }

    @Test
    void 토큰에_권한정보가_있으면_인증객체에_존재한다() {
        //given
        Role role = Role.builder()
            .roleType(RoleType.ROLE_USER)
            .build();

        member.addRole(role);

        String token = jwtTokenService.createToken(member);

        //when
        Authentication authentication = jwtTokenService.getAuthentication(token);

        //then
        assertThat(authentication.getAuthorities().size()).isEqualTo(1);
        assertThat(authentication.getAuthorities()).allMatch(
            auth -> auth.getAuthority().equals(RoleType.ROLE_USER.name()));
    }
}