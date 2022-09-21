package com.practice.smallcommunity.interfaces.post.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostRequest {

    @NotBlank
    private String categoryCode;

    @NotBlank
    private String title;

    @NotBlank
    private String text;
}
