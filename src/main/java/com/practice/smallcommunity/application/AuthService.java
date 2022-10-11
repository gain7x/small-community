package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.dto.AuthDto;
import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.auth.RefreshToken;
import com.practice.smallcommunity.domain.auth.RefreshTokenRepository;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.security.JwtProvider;
import com.practice.smallcommunity.security.dto.TokenDto;
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
public class AuthService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 이메일, 비밀번호가 일치하는 회원을 반환합니다.
     * @param email 이메일
     * @param password 비밀번호
     * @return 회원
     * @throws BusinessException
     *          이메일이 일치하는 회원이 존재하지 않는 경우
     *          이메일이 인증되지 않은 경우
     *          비밀번호가 일치하지 않는 경우
     */
    public AuthDto login(String email, String password) {
        Member findMember = memberService.findByEmail(email);
        if (!findMember.isEmailVerified()) {
            throw new BusinessException(ErrorCode.UNVERIFIED_EMAIL);
        }

        boolean matches = passwordEncoder.matches(password, findMember.getPassword());
        if (!matches) {
            throw new BusinessException(ErrorCode.NOT_MATCH_MEMBER);
        }

        return generateLoginInformation(findMember);
    }

    /**
     * 리프레시 토큰이 유효하면 새로운 로그인 정보를 반환합니다.
     * @param refreshToken 리프레시 토큰
     * @return 로그인 정보
     * @throws BusinessException
     *          갱신이 유효하지 않은 경우
     */
    public AuthDto refresh(String refreshToken) {
        Long memberId;
        try {
            memberId = jwtProvider.getSubject(refreshToken);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        boolean isValidRefreshToken = refreshTokenRepository.existsById(refreshToken);
        if (!isValidRefreshToken) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        refreshTokenRepository.deleteById(refreshToken);
        Member findMember = memberService.findByUserId(memberId);

        return generateLoginInformation(findMember);
    }

    /**
     * 리프레시 토큰을 제거합니다.
     *  호출 후 해당 리프레시 토큰은 더 이상 사용할 수 없습니다.
     * @param refreshToken 제거 대상 리프레시 토큰
     */
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    /**
     * 회원을 기준으로 로그인 정보를 생성합니다.
     * @param member 회원
     * @return 로그인 정보
     */
    private AuthDto generateLoginInformation(Member member) {
        TokenDto accessToken = jwtProvider.createAccessToken(member);
        TokenDto refreshToken = jwtProvider.createRefreshToken(member);
        refreshTokenRepository.save(RefreshToken.builder()
            .token(refreshToken.getToken())
            .member(member)
            .build());

        return AuthDto.builder()
            .accessToken(accessToken.getToken())
            .accessTokenExpires(accessToken.getExpires())
            .refreshToken(refreshToken.getToken())
            .refreshTokenExpires(refreshToken.getExpires())
            .member(member)
            .build();
    }
}
