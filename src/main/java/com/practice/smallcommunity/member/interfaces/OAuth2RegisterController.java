package com.practice.smallcommunity.member.interfaces;

import com.practice.smallcommunity.auth.application.OAuth2LoginService;
import com.practice.smallcommunity.member.application.OAuth2RegistrationTokenService;
import com.practice.smallcommunity.auth.domain.OAuth2Login;
import com.practice.smallcommunity.member.domain.OAuth2RegistrationToken;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.member.domain.MemberRole;
import com.practice.smallcommunity.member.interfaces.dto.OAuth2RegisterRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OAuth2RegisterController {

    private final OAuth2RegistrationTokenService oAuth2RegistrationTokenService;
    private final OAuth2LoginService oauth2LoginService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/oauth2")
    public void register(@Valid @RequestBody OAuth2RegisterRequest dto) {
        OAuth2RegistrationToken token = oAuth2RegistrationTokenService.findOne(dto.getEmail(), dto.getKey());
        Member member = Member.builder()
            .email(token.getEmail())
            .nickname(dto.getNickname())
            .memberRole(MemberRole.USER)
            .build();
        OAuth2Login oAuth2Login = OAuth2Login.builder()
            .member(member)
            .username(token.getUsername())
            .platform(token.getPlatform())
            .build();

        oauth2LoginService.register(oAuth2Login);

        log.info("Social member has been signed up. id: {}, email: {}", member.getId(), member.getEmail());
    }
}
