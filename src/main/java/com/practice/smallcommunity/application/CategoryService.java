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
@Transactional
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
    @Transactional(readOnly = true)
    public Category findOne(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ValidationErrorException("카테고리를 찾을 수 없습니다.",
                ValidationError.of(ValidationErrorStatus.NOT_FOUND, "categoryId")));
    }

    /**
     * 기존 카테고리를 수정합니다.
     * @param categoryId 카테고리 ID
     * @param name 새 이름
     * @param enable 사용상태
     * @return 수정된 카테고리
     * @throws ValidationErrorException
     *          데이터가 유효하지 않은 경우( ID에 해당하는 카테고리가 존재하지 않음, ... )
     */
    public Category update(Long categoryId, String name, boolean enable) {
        Category findCategory = findOne(categoryId);
        findCategory.setEnable(enable);
        findCategory.changeName(name);
        return findCategory;
    }

    /**
     * 카테고리를 삭제 상태로 변경합니다.
     * @param categoryId 카테고리 ID
     * @throws ValidationErrorException
     *          ID에 해당하는 카테고리가 없는 경우
     */
    public void delete(Long categoryId) {
        Category findCategory = findOne(categoryId);
        findCategory.setEnable(false);
    }

    /**
     * 카테고리를 사용 상태로 변경합니다.
     * @param categoryId 카테고리 ID
     * @throws ValidationErrorException
     *          ID에 해당하는 카테고리가 없는 경우
     */
    public void enable(Long categoryId) {
        Category findCategory = findOne(categoryId);
        findCategory.setEnable(true);
    }
}
