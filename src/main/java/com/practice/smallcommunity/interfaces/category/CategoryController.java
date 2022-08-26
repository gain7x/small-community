package com.practice.smallcommunity.interfaces.category;

import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.interfaces.category.dto.CategoryRequest;
import com.practice.smallcommunity.interfaces.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void register(@RequestBody CategoryRequest dto) {
        Category category = mapper.toEntity(dto);
        Category registeredCategory = categoryService.register(category);
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse find(@PathVariable Long categoryId) {
        Category findCategory = categoryService.findOne(categoryId);
        return mapper.toResponse(findCategory);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{categoryId}")
    public void update(@PathVariable Long categoryId, @RequestBody CategoryRequest dto) {
        categoryService.update(categoryId, dto.getName(), dto.isEnable());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
    }
}
