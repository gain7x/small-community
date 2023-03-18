package com.practice.smallcommunity.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.List;
import java.util.Optional;
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

    Member member = DomainGenerator.createMember("A");

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
            .nickname("secondUser")
            .memberRole(MemberRole.USER)
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
    void 탈퇴상태가_아닌_회원을_이메일로_조회한다() {
        //given
        memberRepository.save(member);

        //when
        Member findItem = memberRepository.findByEmailAndWithdrawalIsFalse(this.member.getEmail())
            .orElseThrow();

        //then
        assertThat(findItem.getId()).isEqualTo(member.getId());
        assertThat(findItem.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    void 탈퇴상태가_아닌_회원을_이메일로_조회_시_탈퇴상태인_회원은_조회되지_않는다() {
        //given
        member.withdraw();
        memberRepository.save(member);

        //when
        Optional<Member> findItem = memberRepository.findByEmailAndWithdrawalIsFalse(
            this.member.getEmail());

        //then
        assertThat(findItem.isEmpty()).isTrue();
    }

    @Test
    void 탈퇴상태가_아닌_회원을_ID로_조회한다() {
        //given
        memberRepository.save(member);

        //when
        Member findItem = memberRepository.findByIdAndWithdrawalIsFalse(this.member.getId())
            .orElseThrow();

        //then
        assertThat(findItem.getId()).isEqualTo(member.getId());
    }

    @Test
    void 탈퇴상태가_아닌_회원을_ID로_조회_시_탈퇴상태인_회원은_조회되지_않는다() {
        //given
        member.withdraw();
        memberRepository.save(member);

        //when
        Optional<Member> findItem = memberRepository.findByIdAndWithdrawalIsFalse(this.member.getId());

        //then
        assertThat(findItem.isEmpty()).isTrue();
    }
}