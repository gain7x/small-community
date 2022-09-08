package com.practice.smallcommunity.interfaces.category;

import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.interfaces.BaseResponse;
import com.practice.smallcommunity.interfaces.category.dto.CategoryRequest;
import com.practice.smallcommunity.interfaces.category.dto.CategoryResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void register(@Valid @RequestBody CategoryRequest dto) {
        Category category = mapper.toEntity(dto);
        Category result = categoryService.register(category);

        log.info("Category has been registered. id: {}, code: {}, name: {}, enable: {}",
            result.getId(), result.getCode(), result.getName(), result.isEnable());
    }

    @GetMapping("/{categoryId}")
    public BaseResponse<CategoryResponse> find(@PathVariable Long categoryId) {
        Category findCategory = categoryService.findOne(categoryId);
        return BaseResponse.Ok(mapper.toResponse(findCategory));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{categoryId}")
    public void update(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequest dto) {
        Category result = categoryService.update(categoryId, dto.getName(), dto.isEnable());

        log.info("Update the category. id: {}, name: {} -> {}, enable: {} -> {}",
            categoryId, result.getName(), dto.getName(), result.isEnable(), dto.isEnable());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);

        log.info("Delete a category. id: {}", categoryId);
    }
}
