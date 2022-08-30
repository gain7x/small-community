package com.practice.smallcommunity.interfaces.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostResponse {

    private Long categoryId;
    private Long memberId;
    private String nickname;
    private String title;
    private String text;
    private int views;
    private int votes;
}
