package com.practice.smallcommunity.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.AbstractRedisContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
class RefreshTokenRepositoryTestIT extends AbstractRedisContainerTest {

    @Autowired
    RefreshTokenRepository tokenRepository;

    @Test
    void 저장_및_조회() {
        //given
        RefreshToken refreshToken = RefreshToken.builder()
            .token("some-refresh-token")
            .memberId(1L)
            .build();

        //when
        tokenRepository.save(refreshToken);

        RefreshToken findItem = tokenRepository.findById(refreshToken.getToken())
            .orElseThrow();

        //then
        assertThat(findItem.getToken()).isEqualTo(refreshToken.getToken());
        assertThat(findItem.getMemberId()).isEqualTo(refreshToken.getMemberId());
    }
}