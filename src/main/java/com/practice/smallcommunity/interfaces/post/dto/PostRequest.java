package com.practice.smallcommunity.interfaces.post.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
public class PostRequest {

    @NotBlank
    private String categoryCode;

    @NotBlank
    @Length(max = 20)
    private String title;

    @NotBlank
    private String text;
}
