package com.practice.smallcommunity.interfaces.member.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberContentResponse {

    private long postId;
    private String title;
    private LocalDateTime createdDate;
}
