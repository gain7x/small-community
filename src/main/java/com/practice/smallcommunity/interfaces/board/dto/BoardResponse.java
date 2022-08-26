package com.practice.smallcommunity.interfaces.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BoardResponse {

    private String name;
}
