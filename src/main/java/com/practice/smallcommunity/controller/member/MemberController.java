package com.practice.smallcommunity.controller.member;

import com.practice.smallcommunity.controller.CurrentUser;
import com.practice.smallcommunity.controller.member.dto.MemberDto;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.service.member.MemberService;
import com.practice.smallcommunity.controller.member.dto.MemberRegisterRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void register(@Valid @RequestBody MemberRegisterRequest dto) {
        Member member = mapper.toEntity(dto);
        memberService.registerMember(member);
    }

    @GetMapping
    public MemberDto find(@CurrentUser Long userId) {
        Member member = memberService.findByUserId(userId);
        return mapper.toDto(member);
    }
}
