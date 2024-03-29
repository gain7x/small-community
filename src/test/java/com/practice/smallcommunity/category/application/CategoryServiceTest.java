package com.practice.smallcommunity.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.category.CategoryService;
import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.category.CategoryRepository;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.List;
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
    void 등록_시_동일한_코드의_카테고리가_있으면_예외를_던진다() {
        //given
        when(categoryRepository.existsByCode("dev"))
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
    void 활성_상태인_카테고리를_코드_기준으로_조회한다() {
        //given
        when(categoryRepository.findByCodeAndEnableIsTrue("dev"))
            .thenReturn(Optional.of(category));

        //when
        assertThatNoException().isThrownBy(() -> categoryService.findEnableCategory("dev"));
    }

    @Test
    void 활성_상태인_카테고리를_코드_기준으로_조회할_때_찾지_못한_경우_예외를_던진다() {
        //given
        when(categoryRepository.findByCodeAndEnableIsTrue("dev"))
            .thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> categoryService.findEnableCategory("dev"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_CATEGORY);
    }

    @Test
    void 활성_상태인_모든_카테고리를_조회한다() {
        //given
        when(categoryRepository.findAllByEnableIsTrue())
            .thenReturn(List.of(category));

        //when
        List<Category> result = categoryService.findEnableCategories();

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void 모든_카테고리를_조회한다() {
        //given
        when(categoryRepository.findAll())
            .thenReturn(List.of(category));

        //when
        List<Category> result = categoryService.findAll();

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void 카테고리를_수정한다() {
        //given
        when(categoryRepository.findByCode("dev"))
            .thenReturn(Optional.of(category));

        //when
        Category updateInfo = Category.builder()
            .name("new name")
            .enable(false)
            .build();
        Category updatedCategory = categoryService.update("dev", updateInfo);

        //then
        assertThat(updatedCategory.getName()).isEqualTo("new name");
        assertThat(updatedCategory.isEnable()).isEqualTo(false);
    }

    @Test
    void 카테고리를_삭제한다() {
        //given
        when(categoryRepository.findByCode("dev"))
            .thenReturn(Optional.of(category));

        //when
        assertThatNoException().isThrownBy(() -> categoryService.delete("dev"));
    }
}