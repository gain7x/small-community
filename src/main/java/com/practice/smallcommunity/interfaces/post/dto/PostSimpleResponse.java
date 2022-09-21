package com.practice.smallcommunity.interfaces.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostSimpleResponse {

    private Long postId;
}
