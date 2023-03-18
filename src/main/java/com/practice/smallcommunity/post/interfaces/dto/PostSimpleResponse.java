package com.practice.smallcommunity.post.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostSimpleResponse {

    private Long postId;
}
