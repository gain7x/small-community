package com.practice.smallcommunity.inquiry.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LatestInquiryChatResponse {
    private Long memberId;
    private String nickname;
    private InquiryChatResponse chat;
}
