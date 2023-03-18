package com.practice.smallcommunity.content.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VoteResponse {

    private boolean voted;
}
