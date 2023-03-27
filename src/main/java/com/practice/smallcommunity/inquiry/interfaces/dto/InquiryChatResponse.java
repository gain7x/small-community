package com.practice.smallcommunity.inquiry.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class InquiryChatResponse {

    private Long id;
    private Long senderId;
    private String content;
    private LocalDateTime createdDate;
}
