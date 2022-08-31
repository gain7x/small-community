package com.practice.smallcommunity.application.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.LoginService;
import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.application.exception.ValidationError;
import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    MemberService memberService;

    PasswordEncoder passwordEncoder;
    LoginService loginTokenService;

    Member dummyMember = DomainGenerator.createMember("A");

    @BeforeEach
    void beforeEach() {
        passwordEncoder = mock(PasswordEncoder.class);
        loginTokenService = new LoginService(memberService, passwordEncoder);
    }

    @Test
    void 로그인_성공하면_회원정보_반환() {
        //given
        when(memberService.findByEmail(dummyMember.getEmail()))
            .thenReturn(dummyMember);
        when(passwordEncoder.matches(anyString(), anyString()))
            .thenReturn(true);

        //when
        Member result = loginTokenService.login("userA@mail.com", "userPass");

        //then
        assertThat(result).isEqualTo(dummyMember);
    }

    @Test
    void 회원정보_없으면_검증예외발생() {
        //given
        when(memberService.findByEmail("some@mail.com"))
            .thenThrow(new ValidationErrorException(null, ValidationError.of(null)));

        //when
        //then
        assertThatThrownBy(() -> loginTokenService.login("some@mail.com", "userPass"))
            .isInstanceOf(ValidationErrorException.class);
    }

    @Test
    void 비밀번호_틀리면_검증예외발생() {
        //given
        when(passwordEncoder.matches(anyString(), anyString()))
            .thenReturn(false);

        when(memberService.findByEmail(dummyMember.getEmail()))
            .thenReturn(dummyMember);

        //when
        //then
        assertThatThrownBy(() -> loginTokenService.login("userA@mail.com", "other"))
            .isInstanceOf(ValidationErrorException.class);
    }
}