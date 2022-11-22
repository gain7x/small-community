package com.practice.smallcommunity.domain.auth.oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.AbstractRedisContainerTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
class OAuth2RegistrationTokenRepositoryIT extends AbstractRedisContainerTest {

    @Autowired
    OAuth2RegistrationTokenRepository oAuth2RegistrationTokenRepository;

    @Test
    void 저장_및_조회() {
        //given
        OAuth2RegistrationToken oAuth2RegistrationToken = OAuth2RegistrationToken.builder()
            .key(UUID.randomUUID().toString())
            .email("test@mail.com")
            .username("test")
            .platform(OAuth2Platform.GOOGLE)
            .expirationMinutes(30)
            .build();

        //when
        oAuth2RegistrationTokenRepository.save(oAuth2RegistrationToken);

        OAuth2RegistrationToken findItem = oAuth2RegistrationTokenRepository.findById(oAuth2RegistrationToken.getKey())
            .orElseThrow();

        //then
        assertThat(findItem.getEmail()).isEqualTo("test@mail.com");
    }
}