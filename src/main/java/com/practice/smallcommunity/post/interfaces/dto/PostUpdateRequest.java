package com.practice.smallcommunity.post.interfaces.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
public class PostUpdateRequest {

    @NotBlank
    @Length(max = 75)
    private String title;

    @NotBlank
    private String text;
}
