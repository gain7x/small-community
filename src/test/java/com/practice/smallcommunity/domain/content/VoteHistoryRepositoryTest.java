package com.practice.smallcommunity.domain.content;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.domain.member.MemberRole;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class VoteHistoryRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    VoteHistoryRepository voteHistoryRepository;

    Member member = Member.builder()
        .email("user@mail.com")
        .password("password")
        .nickname("nickname")
        .memberRole(MemberRole.ROLE_USER)
        .build();

    Content content = Content.builder()
        .writer(member)
        .text("컨텐츠")
        .build();

    VoteHistory voteHistory = VoteHistory.builder()
        .voter(member)
        .content(content)
        .positive(true)
        .build();

    @BeforeEach
    void beforeEach() {
        memberRepository.save(member);
        contentRepository.save(content);
    }

    @Test
    void 저장_및_조회() {
        //when
        voteHistoryRepository.save(voteHistory);
        em.flush();
        em.clear();
        VoteHistory findItem = voteHistoryRepository.findById(voteHistory.getId()).orElseThrow();

        //then
        assertThat(voteHistory.getId()).isEqualTo(findItem.getId());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Content content2 = Content.builder()
            .writer(member)
            .text("내용")
            .build();

        contentRepository.save(content2);

        VoteHistory voteHistory2 = VoteHistory.builder()
            .voter(member)
            .content(content2)
            .positive(true)
            .build();

        //when
        voteHistoryRepository.save(voteHistory);
        voteHistoryRepository.save(voteHistory2);

        long count = voteHistoryRepository.count();
        List<VoteHistory> all = voteHistoryRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }


    @Test
    void 동일_회원의_동일한_컨텐츠에_대한_투표기록은_한개만_저장된다() {
        //given
        VoteHistory voteHistory2 = VoteHistory.builder()
            .voter(member)
            .content(content)
            .positive(false)
            .build();

        //when
        voteHistoryRepository.save(voteHistory);
        voteHistoryRepository.save(voteHistory2);

        long count = voteHistoryRepository.count();
        List<VoteHistory> all = voteHistoryRepository.findAll();

        //then
        assertThat(count).isEqualTo(1);
        assertThat(all.size()).isEqualTo(1);
        assertThat(all).allMatch(item -> !item.isPositive());
    }

    @Test
    void 삭제() {
        //given
        voteHistoryRepository.save(voteHistory);

        //when
        VoteHistory findItem = voteHistoryRepository.findById(voteHistory.getId()).orElseThrow();
        voteHistoryRepository.delete(findItem);

        //then
        assertThat(voteHistoryRepository.count()).isEqualTo(0);
    }
}