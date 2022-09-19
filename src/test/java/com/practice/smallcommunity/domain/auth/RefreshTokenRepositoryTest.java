package com.practice.smallcommunity.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RefreshTokenRepository tokenRepository;

    Member dummyMember = DomainGenerator.createMember("A");

    RefreshToken dummyToken = RefreshToken.builder()
        .token("some-refresh-token")
        .member(dummyMember)
        .build();

    @BeforeEach
    void setUp() {
        memberRepository.save(dummyMember);
    }

    @Test
    void 저장_및_조회() {
        //when
        tokenRepository.save(dummyToken);
        em.flush();
        em.clear();
        RefreshToken findItem = tokenRepository.findById(dummyToken.getToken()).orElseThrow();

        //then
        assertThat(findItem.getToken()).isEqualTo(findItem.getToken());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        RefreshToken dummyToken2 = RefreshToken.builder()
            .token("some-refresh-token2")
            .member(dummyMember)
            .build();

        //when
        tokenRepository.save(dummyToken);
        tokenRepository.save(dummyToken2);

        long count = tokenRepository.count();
        List<RefreshToken> all = tokenRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //given
        tokenRepository.save(dummyToken);

        //when
        RefreshToken findItem = tokenRepository.findById(dummyToken.getToken()).orElseThrow();
        tokenRepository.delete(findItem);

        //then
        assertThat(tokenRepository.count()).isEqualTo(0);
    }
}