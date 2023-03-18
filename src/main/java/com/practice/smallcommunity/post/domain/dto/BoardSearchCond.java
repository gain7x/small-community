package com.practice.smallcommunity.post.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BoardSearchCond {

    private Long categoryId;
    private String title;
}
