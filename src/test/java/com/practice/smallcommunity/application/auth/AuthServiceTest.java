package com.practice.smallcommunity.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.auth.dto.AuthDto;
import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.application.member.MemberService;
import com.practice.smallcommunity.domain.auth.RefreshTokenRepository;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.security.JwtProvider;
import com.practice.smallcommunity.security.dto.TokenDto;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    MemberService memberService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    AuthService authService;

    Member dummyMember = DomainGenerator.createMember("A");

    TokenDto dummyAccessToken = TokenDto.builder()
        .token("new-access-token")
        .expires(Date.from(Instant.now().plus(1, ChronoUnit.MINUTES)))
        .build();

    TokenDto dummyRefreshToken = TokenDto.builder()
        .token("new-refresh-token")
        .expires(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        .build();

    @BeforeEach
    void beforeEach() {
        authService = new AuthService(memberService, passwordEncoder, jwtProvider,
            refreshTokenRepository);
    }

    @Test
    void 로그인_성공_시_로그인_정보를_반환한다() {
        //given
        Member spyMember = spy(dummyMember);
        when(spyMember.getId()).thenReturn(1L);

        when(jwtProvider.createAccessToken(spyMember)).thenReturn(dummyAccessToken);
        when(jwtProvider.createRefreshToken(spyMember)).thenReturn(dummyRefreshToken);
        when(memberService.findByEmail(spyMember.getEmail())).thenReturn(spyMember);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        spyMember.verifyEmail();

        //when
        AuthDto result = authService.login("userA@mail.com", "userPass");

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.getMember()).isNotNull();
    }

    @Test
    void 로그인_시_회원정보가_없으면_예외를_던진다() {
        //given
        when(memberService.findByEmail("some@mail.com"))
            .thenThrow(new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        //when
        //then
        assertThatThrownBy(() -> authService.login("some@mail.com", "userPass"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_MEMBER);
    }

    @Test
    void 로그인_시_이메일이_미인증_상태면_예외를_던진다() {
        //given
        when(memberService.findByEmail(dummyMember.getEmail())).thenReturn(dummyMember);

        //when
        //then
        assertThatThrownBy(() -> authService.login("userA@mail.com", "other"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNVERIFIED_EMAIL);
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않으면_예외를_던진다() {
        //given
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(memberService.findByEmail(dummyMember.getEmail())).thenReturn(dummyMember);
        dummyMember.verifyEmail();

        //when
        //then
        assertThatThrownBy(() -> authService.login("userA@mail.com", "other"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_MATCH_MEMBER);
    }

    @Test
    void 재발급이_유효하면_로그인_정보를_반환한다() {
        //given
        Member spyMember = spy(dummyMember);
        when(spyMember.getId()).thenReturn(1L);

        when(jwtProvider.createAccessToken(spyMember)).thenReturn(dummyAccessToken);
        when(jwtProvider.createRefreshToken(spyMember)).thenReturn(dummyRefreshToken);
        when(jwtProvider.getSubject(anyString())).thenReturn(1L);
        when(refreshTokenRepository.existsById(anyString())).thenReturn(true);
        when(memberService.findByUserId(1L)).thenReturn(spyMember);

        //when
        AuthDto result = authService.refresh("refresh-token");

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
        assertThatThrownBy(() -> authService.refresh("refresh-token"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ACCESS_TOKEN);
    }

    @Test
    void 재발급_시_리프레시_토큰이_유효하지_않으면_예외를_던진다() {
        //when
        //then
        assertThatThrownBy(() -> authService.refresh("refresh-token"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @Test
    void 재발급_시_리프레시_토큰이_DB에_없으면_예외를_던진다() {
        //given
        when(refreshTokenRepository.existsById("refresh-token"))
            .thenReturn(false);

        //when
        //then
        assertThatThrownBy(() -> authService.refresh("refresh-token"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
    }
}