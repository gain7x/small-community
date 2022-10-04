package com.practice.smallcommunity.interfaces.content.dto;


import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequest {

    @NotNull
    private Boolean positive;
}
