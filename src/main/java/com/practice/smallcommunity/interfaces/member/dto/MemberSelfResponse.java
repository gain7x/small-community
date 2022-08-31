package com.practice.smallcommunity.interfaces.member.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberSelfResponse {

    private String email;
    private String nickname;
    private LocalDateTime lastPasswordChange;
}
