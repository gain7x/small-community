package com.practice.smallcommunity.domain.board;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.category.CategoryRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BoardRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BoardRepository boardRepository;

    Category category = DomainGenerator.createCategory("개발");
    Board board = DomainGenerator.createBoard(category, "Java");

    @BeforeEach
    void setup() {
        categoryRepository.save(category);
    }

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
            .category(category)
            .name("C++")
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