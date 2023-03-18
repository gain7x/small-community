package com.practice.smallcommunity.post.interfaces.dto;

import java.time.LocalDateTime;
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
    private int replyCount;
    private int votes;
    private Long acceptId;
    private LocalDateTime createdDate;
}
