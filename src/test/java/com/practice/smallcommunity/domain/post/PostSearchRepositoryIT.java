package com.practice.smallcommunity.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.AbstractMySqlContainerTest;
import com.practice.smallcommunity.domain.post.dto.BoardSearchCond;
import com.practice.smallcommunity.infrastructure.PostSearchRepositoryImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MySQL 통합 테스트입니다.
 *  FULLTEXT 인덱스를 테스트합니다. 데이터가 완전히 커밋되어야 FULLTEXT 인덱스가 생성되며,
 *   MATCH-AGAINST 절로 검색이 가능합니다. 이를 위해 트랜잭션을 비활성화하였으니 주의바랍니다.
 */
@ActiveProfiles("tc-db")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataJpaTest
class PostSearchRepositoryIT extends AbstractMySqlContainerTest {

    @PersistenceContext
    EntityManager em;

    PostSearchRepository postSearchRepository;

    @BeforeEach
    void setUp() {
        postSearchRepository = new PostSearchRepositoryImpl(em);
    }

    @Sql(value = "classpath:db/insert-posts.sql")
    @Sql(value = "classpath:db/delete.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void FULLTEXT_인덱스_제목으로_게시글을_조회한다() {
        //given
        BoardSearchCond dto = BoardSearchCond.builder()
            .categoryId(1L)
            .title("some")
            .build();

        Pageable pageable = PageRequest.of(0, 3);

        //when
        Page<Post> result = postSearchRepository.searchPosts(dto, pageable);

        //then
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }
}