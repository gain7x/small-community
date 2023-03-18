package com.practice.smallcommunity.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.auth.application.dto.AuthDto;
import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.member.MemberService;
import com.practice.smallcommunity.auth.domain.RefreshTokenRepository;
import com.practice.smallcommunity.member.Member;
import com.practice.smallcommunity.security.JwtProvider;
import com.practice.smallcommunity.security.dto.TokenDto;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    MemberService memberService;

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
        authService = new AuthService(memberService, jwtProvider, refreshTokenRepository);
    }

    @Test
    void 회원의_자원에_접근할_수_있는_인증_정보를_반환한다() {
        //given
        Member spyMember = spy(dummyMember);
        when(spyMember.getId()).thenReturn(1L);

        when(jwtProvider.createAccessToken(spyMember)).thenReturn(dummyAccessToken);
        when(jwtProvider.createRefreshToken(spyMember)).thenReturn(dummyRefreshToken);

        //when
        AuthDto result = authService.createAuthentication(spyMember);

        //then
        assertThat(result.getMember()).isEqualTo(spyMember);
        assertThat(result.getAccessToken()).isEqualTo(dummyAccessToken.getToken());
        assertThat(result.getRefreshToken()).isEqualTo(dummyRefreshToken.getToken());
    }

    @Test
    void 재발급이_유효하면_로그인_정보를_반환한다() {
        //given
        Member spyMember = spy(dummyMember);
        when(spyMember.getId()).thenReturn(1L);

        when(jwtProvider.createAccessToken(spyMember)).thenReturn(dummyAccessToken);
        when(jwtProvider.createRefreshToken(spyMember)).thenReturn(dummyRefreshToken);
        when(jwtProvider.authenticate(anyString()))
            .thenReturn(new UsernamePasswordAuthenticationToken(1L, null, null));
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
    void 재발급_시_리프레시_토큰이_유효하지_않으면_예외를_던진다() {
        //given
        //when
        //then
        assertThatThrownBy(() -> authService.refresh("refresh-token"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @Test
    void 재발급_시_리프레시_토큰이_DB에_없으면_예외를_던진다() {
        //given
        when(jwtProvider.authenticate(anyString()))
            .thenReturn(new UsernamePasswordAuthenticationToken(1L, null, null));

        when(refreshTokenRepository.existsById("refresh-token"))
            .thenReturn(false);

        //when
        //then
        assertThatThrownBy(() -> authService.refresh("refresh-token"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @Test
    void 리프레시_토큰을_삭제한다() {
        //when
        authService.deleteRefreshToken("refresh-token");

        //then
        verify(refreshTokenRepository, times(1)).deleteById("refresh-token");
    }
}