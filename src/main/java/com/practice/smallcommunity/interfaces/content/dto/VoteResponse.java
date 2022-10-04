package com.practice.smallcommunity.interfaces.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VoteResponse {

    private boolean voted;
}
