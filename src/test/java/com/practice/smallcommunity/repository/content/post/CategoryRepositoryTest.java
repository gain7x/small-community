package com.practice.smallcommunity.repository.content.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.content.post.Category;
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
class CategoryRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void 저장_및_조회() {
        // given
        Category category = Category.builder()
            .name("개발")
            .enabled(true)
            .build();

        // when
        categoryRepository.save(category);
        em.flush();
        em.clear();

        Category findItem = categoryRepository.findById(category.getId()).get();

        // then
        assertThat(category.getId()).isEqualTo(findItem.getId());
        assertThat(category.getName()).isEqualTo(findItem.getName());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Category category = Category.builder()
            .name("개발")
            .enabled(true)
            .build();
        Category category2 = Category.builder()
            .name("일반")
            .enabled(true)
            .build();

        //when
        categoryRepository.save(category);
        categoryRepository.save(category2);

        long count = categoryRepository.count();
        List<Category> all = categoryRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //given
        Category category = Category.builder()
            .name("개발")
            .enabled(true)
            .build();

        //when
        categoryRepository.save(category);
        Category findItem = categoryRepository.findById(category.getId()).get();
        categoryRepository.delete(findItem);

        //then
        assertThat(categoryRepository.count()).isEqualTo(0);
    }
}