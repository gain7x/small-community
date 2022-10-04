package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 관리 서비스를 제공합니다.
 */
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 등록을 시도하고, 성공하면 등록된 회원 정보를 반환합니다.
     * @param member 등록할 회원 정보. 단, id 값은 널이어야 합니다.
     * @return 등록된 회원 정보
     * @throws BusinessException
     *          등록하려는 회원 정보가 유효하지 않은 경우( 아이디 중복, 이메일 중복, ... )
     */
    public Member register(Member member) {
        validateRegisterMember(member);

        String encodePassword = passwordEncoder.encode(member.getPassword());
        member.changePassword(encodePassword);

        return memberRepository.save(member);
    }

    /**
     * ID가 일치하는 회원 정보를 반환합니다.
     * @param userId 회원 번호
     * @return 회원 정보
     * @throws BusinessException
     *          ID가 일치하는 회원이 존재하지 않는 경우
     *          회원이 탈퇴 상태인 경우
     */
    public Member findByUserId(Long userId) {
        return memberRepository.findByIdAndWithdrawalIsFalse(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 이메일이 일치하는 회원 정보를 반환합니다.
     * @param email 이메일
     * @return 회원 정보
     * @throws BusinessException
     *          이메일이 일치하는 회원이 존재하지 않는 경우
     *          회원이 탈퇴 상태인 경우
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmailAndWithdrawalIsFalse(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 회원 정보를 변경합니다.
     * @param userId 회원 번호
     * @param nickname 새로운 별명
     * @return 변경된 회원 정보
     * @throws BusinessException
     *          별명이 다른 회원과 중복되는 경우
     *          ID가 일치하는 회원이 존재하지 않는 경우
     *          회원이 탈퇴 상태인 경우
     */
    public Member update(Long userId, String nickname) {
        Member findMember = findByUserId(userId);
        if (!findMember.getNickname().equals(nickname)) {
            validateNickname(nickname);
            findMember.changeNickname(nickname);
        }

        return findMember;
    }

    /**
     * 회원 암호를 변경합니다.
     * @param userId 회원 번호
     * @param currentPassword 기존 암호
     * @param newPassword 새로운 암호
     * @return 변경된 회원 정보
     * @throws BusinessException
     *          ID가 일치하는 회원이 존재하지 않는 경우
     *          회원이 탈퇴 상태인 경우
     *          기존 비밀번호가 일치하지 않는 경우
     */
    public Member changePassword(Long userId, String currentPassword, String newPassword) {
        Member findMember = findByUserId(userId);
        if (!passwordEncoder.matches(currentPassword, findMember.getPassword())) {
            throw new BusinessException(ErrorCode.NOT_MATCH_MEMBER, "기존 비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        findMember.changePassword(encodedPassword);

        return findMember;
    }

    /**
     * ID가 일치하는 회원을 탈퇴 상태로 변경합니다.
     * @param userId 회원 번호
     * @throws BusinessException
     *          ID가 일치하는 회원이 존재하지 않는 경우
     *          회원이 탈퇴 상태인 경우
     */
    public Member withdrawal(Long userId) {
        Member findMember = findByUserId(userId);
        findMember.withdrawal();
        return findMember;
    }

    private void validateRegisterMember(Member member) {
        validateEmail(member.getEmail());
        validateNickname(member.getNickname());
    }

    private void validateEmail(String email) {
        boolean existsByEmail = memberRepository.existsByEmail(email);
        if (existsByEmail) {
            throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    private void validateNickname(String nickname) {
        boolean existsByNickname = memberRepository.existsByNickname(nickname);
        if (existsByNickname) {
            throw new BusinessException(ErrorCode.DUPLICATED_NICKNAME);
        }
    }
}
