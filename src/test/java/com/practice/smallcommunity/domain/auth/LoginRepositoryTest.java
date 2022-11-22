package com.practice.smallcommunity.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

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
class LoginRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    LoginRepository loginRepository;

    @Autowired
    MemberRepository memberRepository;

    Member member = DomainGenerator.createMember("A");
    Login login = DomainGenerator.createLogin(member);

    @Test
    void 저장_및_조회() {
        //when
        loginRepository.save(login);
        em.flush();
        em.clear();
        Login findItem = loginRepository.findById(login.getId()).orElseThrow();

        //then
        assertThat(login.getId()).isEqualTo(findItem.getId());
        assertThat(login.getPassword()).isEqualTo(findItem.getPassword());
    }

    @Test
    void 엔티티_저장_시_Member_엔티티도_저장된다() {
        //given
        loginRepository.save(login);
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
        Login login2 = DomainGenerator.createLogin(member2);

        //when
        loginRepository.save(login);
        loginRepository.save(login2);

        long count = loginRepository.count();
        List<Login> all = loginRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 엔티티를_회원_ID로_조회한다() {
        //given
        loginRepository.save(login);

        //when
        Optional<Login> result = loginRepository.findByMemberId(member.getId());

        //then
        assertThat(result).isPresent();
    }

    @Test
    void 엔티티를_회원_ID로_조회_시_회원_엔티티를_페치조인한다() {
        //given
        loginRepository.save(login);

        //when
        Login result = loginRepository.findByMemberId(member.getId()).get();

        //then
        assertThat(Hibernate.isInitialized(result.getMember())).isTrue();
    }

    @Test
    void 삭제() {
        //given
        loginRepository.save(login);

        //when
        Login findItem = loginRepository.findById(login.getId()).orElseThrow();
        loginRepository.delete(findItem);

        //then
        assertThat(loginRepository.count()).isEqualTo(0);
    }
}