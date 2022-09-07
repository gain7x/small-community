package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.dto.LoginDto;
import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.login.RefreshToken;
import com.practice.smallcommunity.domain.login.RefreshTokenRepository;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인 요청 시 토큰을 발급하는 서비스입니다.
 */
@RequiredArgsConstructor
@Transactional
@Service
public class LoginService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 이메일 및 암호가 일치하는 회원 정보를 반환합니다.
     * @param email 이메일
     * @param password 암호
     * @return 회원 정보
     * @throws BusinessException
     *          회원이 존재하지 않거나, 암호가 다른 경우
     */
    public LoginDto login(String email, String password) {
        Member findMember = memberService.findByEmail(email);
        boolean matches = passwordEncoder.matches(password, findMember.getPassword());
        if (!matches) {
            throw new BusinessException(ErrorCode.NOT_MATCH_MEMBER);
        }

        return generateLoginInformation(findMember);
    }

    /**
     * 액세스 토큰과 리프레시 토큰을 검증하고, 새로고침이 유효하면 로그인 정보를 발급합니다.
     * @param accessToken 액세스 토큰
     * @param refreshToken 리프레시 토큰
     * @return 로그인 정보
     * @throws BusinessException
     *          새로고침이 유효하지 않은 경우
     */
    public LoginDto refresh(String accessToken, String refreshToken) {
        Long memberId;
        try {
            memberId = jwtProvider.getSubject(accessToken);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        boolean isValidRefreshToken =
            jwtProvider.isValid(refreshToken) && refreshTokenRepository.existsById(refreshToken);
        if (!isValidRefreshToken) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        refreshTokenRepository.deleteById(refreshToken);
        Member findMember = memberService.findByUserId(memberId);

        return generateLoginInformation(findMember);
    }

    /**
     * 회원을 기준으로 로그인 정보를 생성합니다.
     * @param member 회원
     * @return 로그인 정보
     */
    private LoginDto generateLoginInformation(Member member) {
        String accessToken = jwtProvider.createAccessToken(member);
        String refreshToken = jwtProvider.createRefreshToken();
        refreshTokenRepository.save(new RefreshToken(refreshToken));

        return LoginDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .member(member)
            .build();
    }
}
