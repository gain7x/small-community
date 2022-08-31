package com.practice.smallcommunity.application;

import static com.practice.smallcommunity.application.exception.ValidationErrorStatus.NOT_MATCH;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.security.JwtTokenProvider;
import com.practice.smallcommunity.application.exception.ValidationError;
import com.practice.smallcommunity.application.exception.ValidationErrorException;
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

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 입력 정보가 일치하면 JWT 토큰을 반환합니다.
     * @param email 이메일
     * @param password 암호
     * @return JWT 토큰
     * @throws ValidationErrorException
     *          회원이 존재하지 않거나, 암호가 다른 경우
     */
    public String issuance(String email, String password) {
        Member findMember = memberService.findByEmail(email);
        boolean matches = passwordEncoder.matches(password, findMember.getPassword());
        if (!matches) {
            throw new ValidationErrorException("회원 정보가 다릅니다.",
                ValidationError.of(NOT_MATCH));
        }

        return jwtTokenProvider.createToken(findMember);
    }
}
