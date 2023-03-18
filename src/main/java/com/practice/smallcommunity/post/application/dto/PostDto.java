package com.practice.smallcommunity.post.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostDto {

    private String title;
    private String text;
}
