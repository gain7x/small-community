package com.practice.smallcommunity.member.application;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.member.domain.OAuth2RegistrationToken;
import com.practice.smallcommunity.member.domain.OAuth2RegistrationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2RegistrationTokenServiceTest {

    @Mock
    OAuth2RegistrationTokenRepository oAuth2RegistrationTokenRepository;

    OAuth2RegistrationTokenService oAuth2RegistrationTokenService;

    @BeforeEach
    void setUp() {
        oAuth2RegistrationTokenService = new OAuth2RegistrationTokenService(oAuth2RegistrationTokenRepository);
    }

    @Test
    void 소셜_회원의_가입용_토큰을_생성한다() {
        //when
        String result = oAuth2RegistrationTokenService.createRegistrationToken(null, null, null);

        //then
        assertThat(result).isNotBlank();
        verify(oAuth2RegistrationTokenRepository, times(1)).save(any());
    }

    @Test
    void 이메일과_키가_일치하는_가입용_토큰을_검색한다() {
        //given
        when(oAuth2RegistrationTokenRepository.findById("test@mail.com"))
            .thenReturn(Optional.of(OAuth2RegistrationToken.builder().key("key").build()));

        //when
        //then
        assertThatNoException().isThrownBy(() -> {
            OAuth2RegistrationToken result = oAuth2RegistrationTokenService.findOne("test@mail.com", "key");
            assertThat(result).isNotNull();
        });
    }

    @Test
    void 이메일과_키가_일치하는_가입용_토큰이_검색되지_않으면_예외를_던진다() {
        //given
        when(oAuth2RegistrationTokenRepository.findById(any()))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> oAuth2RegistrationTokenService.findOne("test@mail.com", "key"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_OAUTH2_REGISTRATION_KEY);
    }
}