package com.practice.smallcommunity.category.interfaces.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private boolean enable;

    private boolean cudAdminOnly;
}
