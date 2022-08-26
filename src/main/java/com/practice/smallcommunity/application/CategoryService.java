package com.practice.smallcommunity.application;

import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.category.CategoryRepository;
import com.practice.smallcommunity.application.exception.ValidationError;
import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.application.exception.ValidationErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리를 등록하고, 성공하면 등록된 카테고리 데이터를 반환합니다.
     * @param category 카테고리 정보. 단, id 값은 널이어야 합니다.
     * @return 등록된 카테고리
     * @throws ValidationErrorException
     *          등록하려는 카테고리 정보가 유효하지 않은 경우( 카테고리명 중복, ... )
     */
    @Transactional
    public Category register(Category category) {
        boolean existsByName = categoryRepository.existsByName(category.getName());
        if (existsByName) {
            throw new ValidationErrorException("이미 존재하는 카테고리입니다.",
                ValidationError.of(ValidationErrorStatus.DUPLICATED, "name"));
        }

        return categoryRepository.save(category);
    }

    /**
     * 카테고리 번호로 카테고리를 조회합니다.
     * @param categoryId 카테고리 번호
     * @return 카테고리
     * @throws ValidationErrorException
     *          번호에 해당하는 카테고리가 없는 경우
     */
    public Category findOne(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ValidationErrorException("카테고리를 찾을 수 없습니다.",
                ValidationError.of(ValidationErrorStatus.NOT_FOUND, "categoryId")));
    }
}
