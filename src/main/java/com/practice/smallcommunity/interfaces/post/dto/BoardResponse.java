package com.practice.smallcommunity.interfaces.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BoardResponse {

    private Long postId;
    private Long memberId;
    private String nickname;
    private String title;
    private int views;
    private int votes;

    @JsonInclude(Include.NON_NULL)
    private Boolean solved;
}
