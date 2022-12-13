package com.practice.smallcommunity.interfaces.member;

import com.practice.smallcommunity.application.auth.LoginService;
import com.practice.smallcommunity.application.member.MemberService;
import com.practice.smallcommunity.domain.auth.Login;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.interfaces.member.dto.MemberRegisterRequest;
import com.practice.smallcommunity.interfaces.member.dto.MemberUpdateRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/members")
public class MemberAdminController {

    private final MemberService memberService;
    private final LoginService loginService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void register(@Valid @RequestBody MemberRegisterRequest dto) {
        Member member = Member.builder()
            .email(dto.getEmail())
            .nickname(dto.getNickname())
            .memberRole(MemberRole.USER)
            .build();
        Login login = Login.builder()
            .member(member)
            .password(dto.getPassword())
            .build();

        login.verifyEmail();
        loginService.register(login);

        log.info("Admin added a member. member_id: {}, email: {}", member.getId(), member.getEmail());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{memberId}")
    public void update(@PathVariable Long memberId, @Valid @RequestBody MemberUpdateRequest dto) {
        Member result = memberService.update(memberId, dto.getNickname());

        log.info("Admin changed member information. member_id: {}, nickname: {}", memberId, result.getNickname());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{memberId}")
    public void withdrawal(@PathVariable Long memberId) {
        Member result = memberService.withdrawal(memberId);

        log.info("Member has been withdrawn by admin. id: {}, email: {}", memberId, result.getEmail());
    }
}
