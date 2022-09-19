package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
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

    Category category = DomainGenerator.createCategory("dev", "개발");

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
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_CATEGORY);
    }

    @Test
    void 카테고리를_ID로_조회한다() {
        //given
        when(categoryRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(category));

        //when
        //then
        assertThatNoException()
            .isThrownBy(() -> categoryService.findOne(1L));
    }

    @Test
    void 카테고리를_ID로_조회할_때_일치하는_ID가_없으면_예외를_던진다() {
        //given
        when(categoryRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> categoryService.findOne(1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_CATEGORY);
    }

    @Test
    void 카테고리를_수정한다() {
        //given
        when(categoryRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(category));

        //when
        Category updatedCategory = categoryService.update(1L, "new name", false);

        //then
        assertThat(updatedCategory.getName()).isEqualTo("new name");
        assertThat(updatedCategory.isEnable()).isEqualTo(false);
    }

    @Test
    void 카테고리를_삭제한다() {
        //given
        when(categoryRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(category));

        //when
        assertThatNoException().isThrownBy(() -> categoryService.delete(1L));
    }
}