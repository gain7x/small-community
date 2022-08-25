package com.practice.smallcommunity.service.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.security.JwtTokenService;
import com.practice.smallcommunity.service.exception.ValidationErrorException;
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

    @Mock
    JwtTokenService jwtTokenService;

    PasswordEncoder passwordEncoder;
    LoginTokenService loginTokenService;

    @BeforeEach
    void beforeEach() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        loginTokenService = new LoginTokenService(memberRepository, passwordEncoder, jwtTokenService);
    }

    @Test
    void 로그인_성공하면_토큰발행() {
        //given
        Member mockMember = Member.builder()
            .email("userA@mail.com")
            .password(passwordEncoder.encode("userPass"))
            .build();

        when(memberRepository.findByEmail("userA@mail.com"))
            .thenReturn(Optional.of(mockMember));

        String expected = "jwt-token";

        when(jwtTokenService.createToken(mockMember))
            .thenReturn(expected);

        //when
        String token = loginTokenService.issuance("userA@mail.com", "userPass");

        //then
        assertThat(token).isEqualTo(expected);
    }

    @Test
    void 회원정보_없으면_검증예외발생() {
        //given
        when(memberRepository.findByEmail(any(String.class)))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> loginTokenService.issuance("some@mail.com", "userPass"))
            .isInstanceOf(ValidationErrorException.class);
    }

    @Test
    void 비밀번호_틀리면_검증예외발생() {
        //given
        Member mockMember = Member.builder()
            .email("userA@mail.com")
            .password(passwordEncoder.encode("userPass"))
            .build();

        when(memberRepository.findByEmail("userA@mail.com"))
            .thenReturn(Optional.of(mockMember));

        //when
        //then
        assertThatThrownBy(() -> loginTokenService.issuance("userA@mail.com", "other"))
            .isInstanceOf(ValidationErrorException.class);
    }
}