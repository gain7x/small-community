package com.practice.smallcommunity.service.login;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인 요청 시 토큰을 발급하는 서비스입니다.
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginTokenService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    /**
     * 회원 정보를 전달받고, 일치하면 JWT 토큰을 반환합니다.
     * @param username 회원명( ID )
     * @param password 암호
     * @return JWT 토큰
     * @throws IllegalArgumentException
     *          회원이 존재하지 않거나, 암호가 다른 경우
     */
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
