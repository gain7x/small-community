package com.practice.smallcommunity.interfaces.reply.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReplyAddRequest {

    @NotNull
    private Long memberId;

    @NotBlank
    private String text;
}
