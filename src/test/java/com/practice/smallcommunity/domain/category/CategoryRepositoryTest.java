package com.practice.smallcommunity.domain.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.List;
import java.util.Optional;
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

    Category category = DomainGenerator.createCategory("dev", "개발");

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
        Category category2 = DomainGenerator.createCategory("life", "일상");

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
    void ID가_일치하고_삭제상태가_아닌_카테고리를_조회한다() {
        //given
        categoryRepository.save(category);
        em.flush();
        em.clear();

        //when
        Optional<Category> result = categoryRepository.findByIdAndEnableIsTrue(
            this.category.getId());

        //then
        assertThat(result).isPresent();
    }

    @Test
    void ID가_일치하고_삭제상태가_아닌_카테고리를_조회할_때_삭제상태면_조회되지_않는다() {
        //given
        category.setEnable(false);
        categoryRepository.save(category);
        em.flush();
        em.clear();

        //when
        Optional<Category> result = categoryRepository.findByIdAndEnableIsTrue(
            this.category.getId());

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void ID가_일치하고_삭제상태가_아닌_모든_카테고리를_조회한다() {
        //given
        Category category2 = DomainGenerator.createCategory("life", "일상");
        Category category3 = DomainGenerator.createCategory("qna", "Q&A");
        category3.setEnable(false);

        //when
        categoryRepository.save(category);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        long count = categoryRepository.count();
        List<Category> all = categoryRepository.findAllByEnableIsTrue();

        //then
        assertThat(count).isEqualTo(3);
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

    @Test
    void 동일한_이름의_카테고리가_있으면_참을_반환한다() {
        //given
        categoryRepository.save(category);

        //when
        boolean existsByName = categoryRepository.existsByName(category.getName());

        //then
        assertThat(existsByName).isTrue();
    }

    @Test
    void 동일한_이름의_카테고리가_없으면_거짓을_반환한다() {
        //given
        categoryRepository.save(category);

        //when
        boolean existsByName = categoryRepository.existsByName("some category");

        //then
        assertThat(existsByName).isFalse();
    }
}