package com.practice.smallcommunity.application.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.LoginService;
import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.application.dto.LoginDto;
import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.login.RefreshTokenRepository;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.security.JwtProvider;
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

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    LoginService loginService;

    Member dummyMember = DomainGenerator.createMember("A");

    @BeforeEach
    void beforeEach() {
        loginService = new LoginService(memberService, passwordEncoder, jwtProvider,
            refreshTokenRepository);
    }

    @Test
    void 로그인_성공하면_로그인정보_반환() {
        //given
        when(jwtProvider.createAccessToken(dummyMember)).thenReturn("new-access-token");
        when(jwtProvider.createRefreshToken()).thenReturn("new-refresh-token");
        when(memberService.findByEmail(dummyMember.getEmail())).thenReturn(dummyMember);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        //when
        LoginDto result = loginService.login("userA@mail.com", "userPass");

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.getMember()).isNotNull();
    }

    @Test
    void 회원정보_없으면_검증예외발생() {
        //given
        when(memberService.findByEmail("some@mail.com"))
            .thenThrow(new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        //when
        //then
        assertThatThrownBy(() -> loginService.login("some@mail.com", "userPass"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_MEMBER);
    }

    @Test
    void 비밀번호_틀리면_검증예외발생() {
        //given
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(memberService.findByEmail(dummyMember.getEmail())).thenReturn(dummyMember);

        //when
        //then
        assertThatThrownBy(() -> loginService.login("userA@mail.com", "other"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_MATCH_MEMBER);
    }

    @Test
    void 재발급이_유효하면_로그인정보_반환() {
        //given
        when(jwtProvider.createAccessToken(dummyMember)).thenReturn("new-access-token");
        when(jwtProvider.createRefreshToken()).thenReturn("new-refresh-token");
        when(jwtProvider.getSubject(anyString())).thenReturn(1L);
        when(jwtProvider.isValid(anyString())).thenReturn(true);
        when(refreshTokenRepository.existsById(anyString())).thenReturn(true);
        when(memberService.findByUserId(1L)).thenReturn(dummyMember);

        //when
        LoginDto result = loginService.refresh("access-token", "refresh-token");

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.getMember()).isNotNull();
    }

    @Test
    void 재발급_시_액세스_토큰이_유효하지_않으면_예외를_던진다() {
        //given
        when(jwtProvider.getSubject(anyString()))
            .thenThrow(new IllegalArgumentException(""));

        //when
        //then
        assertThatThrownBy(() -> loginService.refresh("access-token", "refresh-token"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ACCESS_TOKEN);
    }

    @Test
    void 재발급_시_리프레시_토큰이_유효하지_않으면_예외를_던진다() {
        //given
        when(jwtProvider.isValid(anyString()))
            .thenReturn(false);

        //when
        //then
        assertThatThrownBy(() -> loginService.refresh("access-token", "refresh-token"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @Test
    void 재발급_시_리프레시_토큰이_DB에_없으면_예외를_던진다() {
        //given
        when(jwtProvider.isValid(anyString()))
            .thenReturn(true);

        when(refreshTokenRepository.existsById("refresh-token"))
            .thenReturn(false);

        //when
        //then
        assertThatThrownBy(() -> loginService.refresh("access-token", "refresh-token"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
    }
}