package com.practice.smallcommunity.interfaces.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequest {

    @NotBlank
    @Length(min = 2, max = 12)
    private String nickname;
}
