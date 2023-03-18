package com.practice.smallcommunity.member.domain;

import com.practice.smallcommunity.testutils.AbstractRedisContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
class OAuth2RegistrationTokenRepositoryIT extends AbstractRedisContainerTest {

    @Autowired
    OAuth2RegistrationTokenRepository oAuth2RegistrationTokenRepository;

    @Test
    void 저장_및_조회() {
        //given
        OAuth2RegistrationToken oAuth2RegistrationToken = OAuth2RegistrationToken.builder()
            .email("test@mail.com")
            .key(UUID.randomUUID().toString())
            .username("test")
            .platform(OAuth2Platform.GOOGLE)
            .expirationMinutes(30)
            .build();

        //when
        oAuth2RegistrationTokenRepository.save(oAuth2RegistrationToken);

        OAuth2RegistrationToken findItem = oAuth2RegistrationTokenRepository.findById("test@mail.com")
            .orElseThrow();

        //then
        assertThat(findItem.getEmail()).isEqualTo("test@mail.com");
    }
}