package com.practice.smallcommunity.application.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.category.CategoryRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    CategoryService categoryService;

    Category category = DomainGenerator.createCategory("개발");

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void 카테고리를_등록한다() {
        //given
        when(categoryRepository.save(category))
            .thenReturn(category);

        //when
        Category registeredCategory = categoryService.register(this.category);

        //then
        assertThat(registeredCategory).isNotNull();
    }

    @Test
    void 등록_시_동일한_이름의_카테고리가_있으면_예외를_던진다() {
        //given
        when(categoryRepository.existsByName("개발"))
            .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> categoryService.register(this.category))
            .isInstanceOf(ValidationErrorException.class);
    }

    @Test
    void 카테고리를_번호로_찾는다() {
        //given
        when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));

        //when
        //then
        assertThatNoException()
            .isThrownBy(() -> categoryService.findOne(1L));
    }

    @Test
    void 번호검색_시_동일한_번호의_카테고리가_없으면_예외를_던진다() {
        //given
        when(categoryRepository.findById(1L))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> categoryService.findOne(1L))
            .isInstanceOf(ValidationErrorException.class);
    }
}