package com.practice.smallcommunity.application.auth;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.application.member.MemberService;
import com.practice.smallcommunity.domain.auth.Login;
import com.practice.smallcommunity.domain.auth.LoginRepository;
import com.practice.smallcommunity.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class LoginService {

    private final LoginRepository loginRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    /**
     * ID로 로그인 엔티티를 조회합니다.
     * @param loginId 로그인 ID
     * @return 로그인 정보
     * @throws BusinessException
     *          ID가 일치하는 로그인 정보가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Login findById(Long loginId) {
        return loginRepository.findById(loginId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_LOGIN));
    }

    /**
     * 회원 ID로 로그인 엔티티를 조회합니다.
     * @param memberId 회원 ID
     * @return 로그인 정보
     * @throws BusinessException
     *          회원 ID가 일치하는 로그인 정보가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Login findByMemberId(Long memberId) {
        return loginRepository.findByMemberId(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 이메일, 비밀번호가 일치하는 로그인 정보를 반환합니다.
     * @param email 이메일
     * @param password 비밀번호
     * @return 로그인 정보
     * @throws BusinessException
     *          이메일이 일치하는 회원이 존재하지 않는 경우
     *          이메일이 인증되지 않은 경우
     *          비밀번호가 일치하지 않는 경우
     */
    public Login login(String email, String password) {
        Member findMember = memberService.findByEmail(email);
        Login findLogin = findByMemberId(findMember.getId());
        if (!findLogin.isEmailVerified()) {
            throw new BusinessException(ErrorCode.UNVERIFIED_EMAIL);
        }
        if (!passwordEncoder.matches(password, findLogin.getPassword())) {
            throw new BusinessException(ErrorCode.NOT_MATCH_MEMBER);
        }

        return findLogin;
    }

    /**
     * 로그인 회원 등록을 시도하고, 성공하면 등록된 로그인 정보를 반환합니다.
     *  비밀번호는 암호화 후 저장됩니다.
     * @param login 등록할 로그인 회원 정보. 단, id 값은 널이어야 합니다.
     * @return 등록된 로그인 정보
     * @throws BusinessException
     *          등록하려는 정보가 유효하지 않은 경우( 이메일 중복, 별명 중복, ... )
     */
    public Login register(Login login) {
        memberService.validateRegistration(login.getMember());

        String encodePassword = passwordEncoder.encode(login.getPassword());
        login.changePassword(encodePassword);

        return loginRepository.save(login);
    }

    /**
     * 로그인 암호를 변경합니다.
     * @param memberId 회원 번호
     * @param currentPassword 기존 암호
     * @param newPassword 새로운 암호
     * @return 변경된 회원 정보
     * @throws BusinessException
     *          ID가 일치하는 회원이 존재하지 않는 경우
     *          회원이 탈퇴 상태인 경우
     *          기존 비밀번호가 일치하지 않는 경우
     */
    public Login changePassword(Long memberId, String currentPassword, String newPassword) {
        Login findLogin = findByMemberId(memberId);
        if (!passwordEncoder.matches(currentPassword, findLogin.getPassword())) {
            throw new BusinessException(ErrorCode.NOT_MATCH_MEMBER, "기존 비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        findLogin.changePassword(encodedPassword);

        return findLogin;
    }

    /**
     * 이메일이 일치하는 로그인 정보를 이메일 인증 상태로 변경합니다.
     * @param email ID
     * @return 로그인 정보
     */
    public Login verifyEmail(String email) {
        Member findMember = memberService.findByEmail(email);
        Login findLogin = findByMemberId(findMember.getId());
        findLogin.verifyEmail();
        return findLogin;
    }
}
