package com.practice.smallcommunity.auth.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.member.application.MemberService;
import com.practice.smallcommunity.auth.domain.OAuth2Login;
import com.practice.smallcommunity.auth.domain.OAuth2LoginRepository;
import com.practice.smallcommunity.member.domain.OAuth2Platform;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginServiceTest {

    @Mock
    OAuth2LoginRepository oauth2LoginRepository;

    @Mock
    MemberService memberService;

    OAuth2LoginService oauth2LoginService;

    Member member = DomainGenerator.createMember("A");
    OAuth2Login oauth2Login = DomainGenerator.createOAuth2Login(member, "test", OAuth2Platform.GOOGLE);

    @BeforeEach
    void setUp() {
        oauth2LoginService = new OAuth2LoginService(oauth2LoginRepository, memberService);
    }

    @Test
    void OAuth2_사용자_ID와_플랫폼이_일치하는_로그인_정보_조회_시_일치하는_정보가_없으면_예외를_던진다() {
        //given
        when(oauth2LoginRepository.findOneFetchJoin(any(), any())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> oauth2LoginService.findOne("test", null))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_OAUTH2_LOGIN);
    }

    @Test
    void OAuth2_회원을_등록한다() {
        //when
        OAuth2Login result = oauth2LoginService.register(oauth2Login);

        //then
        verify(oauth2LoginRepository, times(1)).save(oauth2Login);
    }

    @Test
    void 회원가입_시_이메일이_중복되면_예외를_던진다() {
        //given
        doThrow(new BusinessException(ErrorCode.DUPLICATED_EMAIL))
            .when(memberService).validateRegistration(member);

        //when
        //then
        assertThatThrownBy(() -> oauth2LoginService.register(oauth2Login))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_EMAIL);
    }

    @Test
    void 회원가입_시_별명이_중복되면_예외를_던진다() {
        //given
        doThrow(new BusinessException(ErrorCode.DUPLICATED_NICKNAME))
            .when(memberService).validateRegistration(member);

        //when
        //then
        assertThatThrownBy(() -> oauth2LoginService.register(oauth2Login))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_NICKNAME);
    }
}