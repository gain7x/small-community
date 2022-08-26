package com.practice.smallcommunity.application.dto;

import com.practice.smallcommunity.domain.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BoardDto {

    private Category category;
    private String name;
    private boolean enable;
}
