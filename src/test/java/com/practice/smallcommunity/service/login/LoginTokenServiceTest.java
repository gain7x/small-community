package com.practice.smallcommunity.service.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginTokenServiceTest {

    @Mock
    MemberRepository memberRepository;

    PasswordEncoder passwordEncoder;
    LoginTokenService loginTokenService;

    @BeforeEach
    void beforeEach() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        loginTokenService = new LoginTokenService(memberRepository, passwordEncoder);
    }

    @Test
    void 로그인_성공하면_토큰발행() {
        //given
        Member mockMember = Member.builder()
            .username("userA")
            .password(passwordEncoder.encode("userPass"))
            .build();

        when(memberRepository.findByUsername("userA"))
            .thenReturn(Optional.of(mockMember));

        //when
        String token = loginTokenService.issuance("userA", "userPass");
        Jws<Claims> claims = Jwts.parser()
            .setSigningKey("dummy")
            .parseClaimsJws(token);

        //then
        assertThat(claims.getBody().getSubject()).isEqualTo("userA");
    }

    @Test
    void 회원정보_없으면_예외발생() {
        //given
        when(memberRepository.findByUsername(any(String.class)))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> loginTokenService.issuance("some", "userPass"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("회원 정보가 없습니다.");
    }

    @Test
    void 비밀번호_틀리면_예외발생() {
        //given
        Member mockMember = Member.builder()
            .username("userA")
            .password(passwordEncoder.encode("userPass"))
            .build();

        when(memberRepository.findByUsername("userA"))
            .thenReturn(Optional.of(mockMember));

        //when
        //then
        assertThatThrownBy(() -> loginTokenService.issuance("userA", "other"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("회원 정보가 다릅니다.");
    }
}