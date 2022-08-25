package com.practice.smallcommunity.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    Member member = Member.builder()
        .email("userA@mail.com")
        .password("password")
        .nickname("firstUser")
        .memberRole(MemberRole.ROLE_USER)
        .build();

    @Test
    void 저장_및_조회() {
        //when
        memberRepository.save(member);
        em.flush();
        em.clear();
        Member findItem = memberRepository.findById(member.getId()).orElseThrow();

        //then
        assertThat(member.getId()).isEqualTo(findItem.getId());
        assertThat(member.getEmail()).isEqualTo(findItem.getEmail());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Member member2 = Member.builder()
            .email("userB@mail.com")
            .password("password")
            .nickname("secondUser")
            .memberRole(MemberRole.ROLE_USER)
            .build();

        //when
        memberRepository.save(member);
        memberRepository.save(member2);

        long count = memberRepository.count();
        List<Member> all = memberRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //given
        memberRepository.save(member);

        //when
        Member findItem = memberRepository.findById(member.getId()).orElseThrow();
        memberRepository.delete(findItem);

        //then
        assertThat(memberRepository.count()).isEqualTo(0);
    }

    @Test
    void 회원명으로_조회() {
        //given
        memberRepository.save(member);

        //when
        Member findItem = memberRepository.findByEmail(member.getEmail()).orElseThrow();

        assertThat(findItem.getId()).isEqualTo(member.getId());
        assertThat(findItem.getEmail()).isEqualTo(findItem.getEmail());
    }

    @Test
    void 이메일로_조회() {
        //given
        memberRepository.save(member);

        //when
        Member findItem = memberRepository.findByEmail(this.member.getEmail()).orElseThrow();

        //then
        assertThat(findItem.getId()).isEqualTo(member.getId());
        assertThat(findItem.getEmail()).isEqualTo(member.getEmail());
    }
}