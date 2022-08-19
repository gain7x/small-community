package com.practice.smallcommunity.service.login;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.repository.member.MemberRepository;
import com.practice.smallcommunity.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginTokenService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public String issuance(String username, String password) {
        Member findMember = memberRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        boolean matches = passwordEncoder.matches(password, findMember.getPassword());
        if (!matches) {
            throw new IllegalArgumentException("회원 정보가 다릅니다.");
        }

        return jwtTokenService.createToken(findMember);
    }
}
