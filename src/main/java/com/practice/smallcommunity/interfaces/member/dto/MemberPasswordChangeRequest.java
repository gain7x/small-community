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
public class MemberPasswordChangeRequest {

    @NotBlank
    @Length(min = 8, max = 18)
    private String currentPassword;

    @NotBlank
    @Length(min = 8, max = 18)
    private String newPassword;
}
