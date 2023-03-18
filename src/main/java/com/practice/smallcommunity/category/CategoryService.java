package com.practice.smallcommunity.category;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;

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
        boolean existsByCode = categoryRepository.existsByCode(category.getCode());
        if (existsByCode) {
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
     *          코드가 일치하는 카테고리가 없는 경우
     */
    @Transactional(readOnly = true)
    public Category findOne(String categoryCode) {
        return categoryRepository.findByCode(categoryCode)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    /**
     * 카테고리 코드로 활성 상태인 카테고리를 조회합니다.
     * @param categoryCode 카테고리 코드
     * @return 카테고리
     * @throws BusinessException
     *          코드가 일치하는 카테고리가 없는 경우
     *          카테고리가 비활성 상태인 경우
     */
    @Transactional(readOnly = true)
    public Category findEnableCategory(String categoryCode) {
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
     * 모든 카테고리를 조회합니다.
     * @return 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * 기존 카테고리를 수정합니다.
     * @param categoryCode 카테고리 코드
     * @param category 수정할 정보
     * @return 수정된 카테고리
     * @throws BusinessException
     *          코드가 일치하는 카테고리가 없는 경우
     */
    public Category update(String categoryCode, Category category) {
        Category findCategory = findOne(categoryCode);
        findCategory.changeCode(category.getCode());
        findCategory.changeName(category.getName());
        findCategory.setEnable(category.isEnable());
        findCategory.setCudAdminOnly(category.isCudAdminOnly());
        return findCategory;
    }

    /**
     * 카테고리를 삭제합니다.
     * @param categoryCode 카테고리 코드
     * @throws BusinessException
     *          코드가 일치하는 카테고리가 없는 경우
     */
    public void delete(String categoryCode) {
        Category findCategory = findOne(categoryCode);
        categoryRepository.delete(findCategory);
    }
}
