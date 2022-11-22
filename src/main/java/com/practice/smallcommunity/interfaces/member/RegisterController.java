package com.practice.smallcommunity.interfaces.member;

import com.practice.smallcommunity.application.auth.LoginService;
import com.practice.smallcommunity.application.auth.MailVerificationService;
import com.practice.smallcommunity.domain.auth.Login;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RegisterController {

    private final MailVerificationService mailVerificationService;
    private final LoginService loginService;

    @PostMapping("${verification.mail.api}")
    public String verifyEmail(@NotEmpty String key) {
        MailVerification verification = mailVerificationService.check(key);
        Login result = loginService.verifyEmail(verification.getEmail());

        log.info("Member email has been verified. id: {}, email: {}",
            result.getMember().getId(), verification.getEmail());

        return "verify-email";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/members")
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

        loginService.register(login);
        mailVerificationService.sendVerificationMail(member.getEmail());

        log.info("Member has been signed up. id: {}, email: {}", member.getId(), member.getEmail());
    }
}
