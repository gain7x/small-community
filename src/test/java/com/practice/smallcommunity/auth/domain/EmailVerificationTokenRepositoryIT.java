package com.practice.smallcommunity.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.testutils.AbstractRedisContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
class EmailVerificationTokenRepositoryIT extends AbstractRedisContainerTest {

    @Autowired
    EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Test
    void 저장_및_조회() {
        //given
        EmailVerificationToken verification = EmailVerificationToken.builder()
            .email("test@mail.com")
            .key("key")
            .build();

        //when
        emailVerificationTokenRepository.save(verification);

        EmailVerificationToken findItem = emailVerificationTokenRepository.findById(
            verification.getEmail()).orElseThrow();

        //then
        assertThat(findItem.getEmail()).isEqualTo("test@mail.com");
    }
}