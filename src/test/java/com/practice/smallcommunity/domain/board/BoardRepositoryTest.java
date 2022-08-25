package com.practice.smallcommunity.domain.board;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BoardRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    BoardRepository boardRepository;

    Board board = Board.builder()
        .name("개발")
        .code("DEV")
        .enable(true)
        .build();

    @Test
    void 저장_및_조회() {
        // when
        boardRepository.save(board);
        em.flush();
        em.clear();

        Board findItem = boardRepository.findById(board.getId()).orElseThrow();

        // then
        assertThat(board.getId()).isEqualTo(findItem.getId());
        assertThat(board.getName()).isEqualTo(findItem.getName());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Board board2 = Board.builder()
            .name("일반")
            .code("NORMAL")
            .enable(true)
            .build();

        //when
        boardRepository.save(board);
        boardRepository.save(board2);

        long count = boardRepository.count();
        List<Board> all = boardRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //when
        boardRepository.save(board);
        Board findItem = boardRepository.findById(board.getId()).orElseThrow();
        boardRepository.delete(findItem);

        //then
        assertThat(boardRepository.count()).isEqualTo(0);
    }
}