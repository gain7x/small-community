package com.practice.smallcommunity.interfaces.category;

import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.interfaces.category.dto.CategoryRequest;
import com.practice.smallcommunity.interfaces.category.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    Category toEntity(CategoryRequest dto);
}
