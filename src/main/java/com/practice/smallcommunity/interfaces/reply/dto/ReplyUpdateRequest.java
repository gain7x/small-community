package com.practice.smallcommunity.interfaces.reply.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReplyUpdateRequest {

    @NotBlank
    private String text;
}
