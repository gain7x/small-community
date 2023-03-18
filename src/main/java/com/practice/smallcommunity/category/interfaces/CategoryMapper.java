package com.practice.smallcommunity.category.interfaces;

import com.practice.smallcommunity.category.interfaces.dto.CategoryRequest;
import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.category.interfaces.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    Category toEntity(CategoryRequest dto);
}
