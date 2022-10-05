package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.category.CategoryRepository;
import java.util.List;
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
     * @throws BusinessException
     *          등록하려는 카테고리 정보가 유효하지 않은 경우( 카테고리명 중복, ... )
     */
    public Category register(Category category) {
        boolean existsByName = categoryRepository.existsByName(category.getName());
        if (existsByName) {
            throw new BusinessException(ErrorCode.DUPLICATED_CATEGORY);
        }

        return categoryRepository.save(category);
    }

    /**
     * 카테고리 번호로 카테고리를 조회합니다.
     * @param categoryId 카테고리 번호
     * @return 카테고리
     * @throws BusinessException
     *          번호에 해당하는 카테고리가 없는 경우
     */
    @Transactional(readOnly = true)
    public Category findOne(Long categoryId) {
        return categoryRepository.findByIdAndEnableIsTrue(categoryId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    /**
     * 카테고리 코드로 카테고리를 조회합니다.
     * @param categoryCode 카테고리 코드
     * @return 카테고리
     * @throws BusinessException
     *          번호에 해당하는 카테고리가 없는 경우
     */
    @Transactional(readOnly = true)
    public Category findOne(String categoryCode) {
        return categoryRepository.findByCodeAndEnableIsTrue(categoryCode)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    /**
     * 삭제상태가 아닌 모든 카테고리를 조회합니다.
     * @return 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<Category> findEnableCategories() {
        return categoryRepository.findAllByEnableIsTrue();
    }

    /**
     * 기존 카테고리를 수정합니다.
     * @param categoryId 카테고리 ID
     * @param name 새 이름
     * @param enable 사용상태
     * @return 수정된 카테고리
     * @throws BusinessException
     *          데이터가 유효하지 않은 경우( ID에 해당하는 카테고리가 존재하지 않음, ... )
     */
    public Category update(Long categoryId, String name, boolean enable) {
        Category findCategory = findOne(categoryId);
        findCategory.changeName(name);
        findCategory.setEnable(enable);
        return findCategory;
    }

    /**
     * 카테고리를 삭제합니다.
     * @param categoryId 카테고리 ID
     * @throws BusinessException
     *          ID에 해당하는 카테고리가 없는 경우
     */
    public void delete(Long categoryId) {
        Category findCategory = findOne(categoryId);
        categoryRepository.delete(findCategory);
    }
}
