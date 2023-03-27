package com.practice.smallcommunity.inquiry.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class InquiryChatMessage {

    private Long inquirerId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
}
