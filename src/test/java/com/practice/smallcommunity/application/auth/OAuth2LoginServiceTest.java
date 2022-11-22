package com.practice.smallcommunity.application.auth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2LoginRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginServiceTest {

    @Mock
    OAuth2LoginRepository oAuth2LoginRepository;

    OAuth2LoginService oAuth2LoginService;

    @BeforeEach
    void setUp() {
        oAuth2LoginService = new OAuth2LoginService(oAuth2LoginRepository);
    }

    @Test
    void OAuth2_사용자_ID와_플랫폼이_일치하는_로그인_정보_조회_시_일치하는_정보가_없으면_예외를_던진다() {
        //given
        when(oAuth2LoginRepository.findByUsernameAndPlatform(any(), any())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> oAuth2LoginService.findOne("test", null))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_OAUTH2_LOGIN);
    }
}