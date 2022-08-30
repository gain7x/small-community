package com.practice.smallcommunity.interfaces.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReplyResponse {

    private Long replyId;
    private Long memberId;
    private String nickname;
    private String text;
    private int votes;
}
