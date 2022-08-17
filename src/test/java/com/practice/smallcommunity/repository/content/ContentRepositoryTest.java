package com.practice.smallcommunity.repository.content;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.content.Content;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ContentRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ContentRepository contentRepository;

    Content content = Content.builder()
        .text("컨텐츠")
        .build();

    @Test
    void 저장_및_조회() {
        //when
        contentRepository.save(content);
        em.flush();
        em.clear();
        Content findItem = contentRepository.findById(content.getId()).get();

        //then
        assertThat(content.getId()).isEqualTo(findItem.getId());
        assertThat(content.getText()).isEqualTo(findItem.getText());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Content content2 = Content.builder()
            .text("컨텐츠2")
            .build();

        //when
        contentRepository.save(content);
        contentRepository.save(content2);

        long count = contentRepository.count();
        List<Content> all = contentRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //given
        contentRepository.save(content);

        //when
        Content findItem = contentRepository.findById(content.getId()).get();
        contentRepository.delete(findItem);

        //then
        assertThat(contentRepository.count()).isEqualTo(0);
    }
}