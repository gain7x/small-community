package com.practice.smallcommunity.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Login;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2LoginRepository;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OAuth2LoginRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    OAuth2LoginRepository oAuth2LoginRepository;

    @Autowired
    MemberRepository memberRepository;

    Member member = DomainGenerator.createMember("A");
    OAuth2Login oAuth2Login = DomainGenerator.createOAuth2Login(member, "test", OAuth2Platform.GOOGLE);

    @Test
    void 저장_및_조회() {
        //when
        oAuth2LoginRepository.save(oAuth2Login);
        em.flush();
        em.clear();
        OAuth2Login findItem = oAuth2LoginRepository.findById(oAuth2Login.getId()).orElseThrow();

        //then
        assertThat(oAuth2Login.getId()).isEqualTo(findItem.getId());
        assertThat(oAuth2Login.getUsername()).isEqualTo(findItem.getUsername());
    }

    @Test
    void 엔티티_저장_시_회원_엔티티도_저장된다() {
        //given
        oAuth2LoginRepository.save(oAuth2Login);
        em.flush();
        em.clear();

        //when
        Optional<Member> result = memberRepository.findById(member.getId());

        //then
        assertThat(result).isPresent();
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Member member2 = DomainGenerator.createMember("B");
        OAuth2Login oAuth2Login2 = DomainGenerator.createOAuth2Login(member2, "test2", OAuth2Platform.GOOGLE);

        //when
        oAuth2LoginRepository.save(oAuth2Login);
        oAuth2LoginRepository.save(oAuth2Login2);

        long count = oAuth2LoginRepository.count();
        List<OAuth2Login> all = oAuth2LoginRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 사용자명과_플랫폼이_일치하는_엔티티를_조회한다() {
        //given
        oAuth2LoginRepository.save(oAuth2Login);

        //when
        Optional<OAuth2Login> result = oAuth2LoginRepository.findOneFetchJoin(
            oAuth2Login.getUsername(), oAuth2Login.getPlatform());

        //then
        assertThat(result).isPresent();
    }

    @Test
    void 사용자명과_플랫폼이_일치하는_엔티티_조회_시_회원_엔티티를_페치조인한다() {
        //given
        oAuth2LoginRepository.save(oAuth2Login);

        //when
        OAuth2Login result = oAuth2LoginRepository.findOneFetchJoin(oAuth2Login.getUsername(),
            oAuth2Login.getPlatform()).get();

        //then
        assertThat(Hibernate.isInitialized(result.getMember())).isTrue();
    }

    @Test
    void 삭제() {
        //given
        oAuth2LoginRepository.save(oAuth2Login);

        //when
        OAuth2Login findItem = oAuth2LoginRepository.findById(oAuth2Login.getId()).orElseThrow();
        oAuth2LoginRepository.delete(findItem);

        //then
        assertThat(oAuth2LoginRepository.count()).isEqualTo(0);
    }
}