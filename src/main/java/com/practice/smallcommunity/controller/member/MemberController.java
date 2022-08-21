package com.practice.smallcommunity.controller.member;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.service.member.MemberService;
import com.practice.smallcommunity.controller.member.dto.MemberRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@RequestBody MemberRegisterDto dto) {
        Member member = mapper.toEntity(dto);
        memberService.registerMember(member);
    }
}
