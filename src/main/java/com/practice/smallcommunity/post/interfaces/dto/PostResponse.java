package com.practice.smallcommunity.post.interfaces.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostResponse {

    private String categoryCode;
    private Long memberId;
    private String nickname;
    private String title;
    private String text;
    private int views;
    private int votes;
    private Long acceptId;
    private LocalDateTime createdDate;
}
