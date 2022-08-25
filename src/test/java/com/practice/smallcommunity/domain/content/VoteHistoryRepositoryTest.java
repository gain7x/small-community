package com.practice.smallcommunity.domain.content;

import static org.assertj.core.api.Assertions.assertThat;

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
    ContentRepository contentRepository;

    @Autowired
    VoteHistoryRepository voteHistoryRepository;

    Content content = Content.builder()
        .text("컨텐츠")
        .build();

    VoteHistory voteHistory = VoteHistory.builder()
        .voterId(1L)
        .content(content)
        .positive(true)
        .build();

    @BeforeEach
    void beforeEach() {
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
        assertThat(voteHistory.getVoterId()).isEqualTo(findItem.getVoterId());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        VoteHistory voteHistory2 = VoteHistory.builder()
            .voterId(2L)
            .content(content)
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