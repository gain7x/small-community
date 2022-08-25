package com.practice.smallcommunity.domain.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CategoryRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    CategoryRepository categoryRepository;

    Category category = DomainGenerator.createCategory("개발");

    @Test
    void 저장_및_조회() {
        //when
        categoryRepository.save(category);
        em.flush();
        em.clear();
        Category findItem = categoryRepository.findById(category.getId()).orElseThrow();

        //then
        assertThat(category.getId()).isEqualTo(findItem.getId());
        assertThat(category.getName()).isEqualTo(findItem.getName());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Category category2 = Category.builder()
            .name("일상")
            .enable(true)
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
        categoryRepository.save(category);

        //when
        Category findItem = categoryRepository.findById(category.getId()).orElseThrow();
        categoryRepository.delete(findItem);

        //then
        assertThat(categoryRepository.count()).isEqualTo(0);
    }
}