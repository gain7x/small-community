package com.practice.smallcommunity.interfaces.board.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BoardRequest {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String name;

    private boolean enable;
}
