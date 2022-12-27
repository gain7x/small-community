package com.practice.smallcommunity.security.converter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import com.practice.smallcommunity.security.user.SocialUser;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class DelegatingSocialUserConverterTest {

    DelegatingSocialUserConverter delegatingSocialUserConverter = new DelegatingSocialUserConverter();

    @Mock
    OAuth2User oauth2User;

    @Test
    void 구글_사용자를_변환한다() {
        //given
        Map<String, Object> googleAttributes = Map.of("sub", "testUsername", "email", "test@mail.com");
        when(oauth2User.getAttributes()).thenReturn(googleAttributes);
        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(oauth2User, null,
            OAuth2Platform.GOOGLE.getRegistrationId());

        //when
        SocialUser socialUser = delegatingSocialUserConverter.convert(authenticationToken);

        //then
        assertThat(socialUser.getUsername()).isEqualTo("testUsername");
        assertThat(socialUser.getEmail()).isEqualTo("test@mail.com");
        assertThat(socialUser.getPlatform()).isEqualTo(OAuth2Platform.GOOGLE);
    }

    @Test
    void 네이버_사용자를_변환한다() {
        //given
        Map<String, Object> naverAttributes = Map.of("response",
            Map.of("id", "testUsername", "email", "test@mail.com"));
        when(oauth2User.getAttributes()).thenReturn(naverAttributes);
        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(oauth2User, null,
            OAuth2Platform.NAVER.getRegistrationId());

        //when
        SocialUser socialUser = delegatingSocialUserConverter.convert(authenticationToken);

        //then
        assertThat(socialUser.getUsername()).isEqualTo("testUsername");
        assertThat(socialUser.getEmail()).isEqualTo("test@mail.com");
        assertThat(socialUser.getPlatform()).isEqualTo(OAuth2Platform.NAVER);
    }

    @Test
    void RegistrationId가_일치하는_클라이언트가_없으면_null을_반환한다() {
        //given
        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(oauth2User, null,
            "test");

        //when
        SocialUser convert = delegatingSocialUserConverter.convert(authenticationToken);

        //then
        assertThat(convert).isNull();
    }
}