package com.practice.smallcommunity.interfaces.member;

import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.interfaces.BaseResponse;
import com.practice.smallcommunity.interfaces.member.dto.MemberRegisterRequest;
import com.practice.smallcommunity.interfaces.member.dto.MemberResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        member.changeMemberRole(MemberRole.USER);
        memberService.register(member);
    }

    @GetMapping("/{userId}")
    public BaseResponse<MemberResponse> find(@PathVariable Long userId) {
        Member member = memberService.findByUserId(userId);
        return BaseResponse.Ok(mapper.toDto(member));
    }
}
