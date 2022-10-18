package com.practice.smallcommunity.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.AbstractRedisContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
class MailVerificationRepositoryTestIT extends AbstractRedisContainerTest {

    @Autowired
    MailVerificationRepository mailVerificationRepository;

    @Test
    void 저장_및_조회() {
        //given
        MailVerification verification = MailVerification.builder()
            .key("key")
            .email("test@mail.com")
            .build();

        //when
        mailVerificationRepository.save(verification);

        MailVerification findItem = mailVerificationRepository.findById(
            verification.getKey()).orElseThrow();

        //then
        assertThat(findItem.getEmail()).isEqualTo("test@mail.com");
    }
}