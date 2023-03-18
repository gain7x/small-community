package com.practice.smallcommunity.category.interfaces;

import com.practice.smallcommunity.category.CategoryService;
import com.practice.smallcommunity.category.interfaces.dto.CategoryRequest;
import com.practice.smallcommunity.category.interfaces.dto.CategoryResponse;
import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.common.interfaces.dto.BaseResponse;
import com.practice.smallcommunity.common.interfaces.dto.CollectionResponse;

import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {

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

    @GetMapping
    public CollectionResponse<CategoryResponse> findAll() {
        List<Category> categories = categoryService.findAll();
        List<CategoryResponse> result = categories.stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return CollectionResponse.Ok(result);
    }

    @GetMapping("/{categoryId}")
    public BaseResponse<CategoryResponse> find(@PathVariable Long categoryId) {
        Category findCategory = categoryService.findOne(categoryId);
        return BaseResponse.Ok(mapper.toResponse(findCategory));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{categoryCode}")
    public void update(@PathVariable String categoryCode, @Valid @RequestBody CategoryRequest dto) {
        Category result = categoryService.update(categoryCode, mapper.toEntity(dto));

        log.info("Update the category. code: {} -> {}", categoryCode, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{categoryCode}")
    public void delete(@PathVariable String categoryCode) {
        categoryService.delete(categoryCode);

        log.info("Delete a category. id: {}", categoryCode);
    }
}
