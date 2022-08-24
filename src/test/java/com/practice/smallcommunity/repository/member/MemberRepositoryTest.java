package com.practice.smallcommunity.repository.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DataJpaTest
class MemberRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    Member member = Member.builder()
        .username("userA")
        .password("password")
        .email("userA@mail.com")
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
        assertThat(member.getUsername()).isEqualTo(findItem.getUsername());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Member member2 = Member.builder()
            .username("userB")
            .password("password")
            .email("userB@mail.com")
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
        Member findItem = memberRepository.findByUsername(member.getUsername()).orElseThrow();

        assertThat(findItem.getId()).isEqualTo(member.getId());
        assertThat(findItem.getUsername()).isEqualTo(findItem.getUsername());
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