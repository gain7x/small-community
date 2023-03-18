package com.practice.smallcommunity.reply.interfaces.dto;

import java.time.LocalDateTime;
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
    private LocalDateTime createdDate;
}
