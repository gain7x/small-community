package com.practice.smallcommunity.interfaces.post.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostRequest {

    @NotNull
    private Long boardId;

    @NotNull
    private Long memberId;

    @NotBlank
    private String title;

    @NotBlank
    private String text;
}
