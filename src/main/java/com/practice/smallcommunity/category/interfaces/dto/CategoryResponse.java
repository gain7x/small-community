package com.practice.smallcommunity.category.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private String code;
    private String name;
    private boolean enable;
    private boolean cudAdminOnly;
}
