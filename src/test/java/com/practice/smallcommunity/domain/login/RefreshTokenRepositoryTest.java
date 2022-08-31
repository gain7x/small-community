package com.practice.smallcommunity.domain.login;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    RefreshTokenRepository tokenRepository;

    RefreshToken token = new RefreshToken("refresh-token");

    @Test
    void 저장_및_조회() {
        //when
        tokenRepository.save(token);
        em.flush();
        em.clear();
        RefreshToken findItem = tokenRepository.findById(token.getToken()).orElseThrow();

        //then
        assertThat(findItem.getToken()).isEqualTo(findItem.getToken());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        RefreshToken token2 = new RefreshToken("refresh-token2");

        //when
        tokenRepository.save(token);
        tokenRepository.save(token2);

        long count = tokenRepository.count();
        List<RefreshToken> all = tokenRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //given
        tokenRepository.save(token);

        //when
        RefreshToken findItem = tokenRepository.findById(token.getToken()).orElseThrow();
        tokenRepository.delete(findItem);

        //then
        assertThat(tokenRepository.count()).isEqualTo(0);
    }
}