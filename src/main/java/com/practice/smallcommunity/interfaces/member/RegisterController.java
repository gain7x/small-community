package com.practice.smallcommunity.interfaces.member;

import com.practice.smallcommunity.application.MailVerificationService;
import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.domain.auth.MailVerification;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.interfaces.member.dto.MemberRegisterRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping
public class RegisterController {

    private final MailVerificationService mailVerificationService;
    private final MemberService memberService;
    private final MemberMapper mapper;

    @PostMapping("${verification.mail.api}")
    public String sendVerificationMail(@NotEmpty String key) {
        MailVerification verification = mailVerificationService.check(key);
        Member result = memberService.verifyEmail(verification.getMail());

        log.info("Member email has been verified. id: {}, email: {}",
            result.getId(), verification.getMail());

        return "verify-email";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/members")
    public void register(@Valid @RequestBody MemberRegisterRequest dto) {
        Member member = mapper.toEntity(dto);
        member.changeMemberRole(MemberRole.USER);
        Member result = memberService.register(member);

        mailVerificationService.sendVerificationMail(result.getEmail());

        log.info("Member has been signed up. id: {}, email: {}", result.getId(), result.getEmail());
    }
}
