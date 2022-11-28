package com.practice.smallcommunity.interfaces.member;

import com.practice.smallcommunity.application.auth.EmailVerificationService;
import com.practice.smallcommunity.application.auth.LoginService;
import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.auth.EmailVerificationToken;
import com.practice.smallcommunity.domain.auth.Login;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.interfaces.member.dto.EmailVerificationRequest;
import com.practice.smallcommunity.interfaces.member.dto.MemberRegisterRequest;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RegisterController {

    private final EmailVerificationService emailVerificationService;
    private final LoginService loginService;

    @PostMapping("${verification.email.api}")
    public void verifyEmail(HttpServletResponse response, @Valid EmailVerificationRequest dto) throws IOException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(dto.getRedirectUri());

        try {
            EmailVerificationToken verificationToken = emailVerificationService.check(dto.getEmail(), dto.getKey());
            Login result = loginService.verifyEmail(verificationToken.getEmail());

            log.info("Login email has been verified. id: {}, email: {}",
                result.getId(), verificationToken.getEmail());

            builder.queryParam("verifiedEmail", dto.getEmail());
        } catch (BusinessException e) {
            builder.queryParam("error", e.getErrorCode().getCode());
        } catch (RuntimeException e) {
            builder.queryParam("error", ErrorCode.RUNTIME_ERROR.getCode());
        }

        response.sendRedirect(builder.toUriString());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/members")
    public void register(HttpServletRequest request, @Valid @RequestBody MemberRegisterRequest dto) {
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
        emailVerificationService.sendVerificationMail(request, dto.getEmail(), dto.getRedirectUri());

        log.info("Member has been signed up. id: {}, email: {}", member.getId(), member.getEmail());
    }
}
