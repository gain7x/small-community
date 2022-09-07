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
     */
    public Member findByUserId(Long userId) {
        return memberRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 이메일이 일치하는 회원 정보를 반환합니다.
     * @param email 이메일
     * @return 회원 정보
     * @throws BusinessException
     *          이메일이 일치하는 회원이 존재하지 않는 경우
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    }

    private void validateRegisterMember(Member member) {
        boolean existsByEmail = memberRepository.existsByEmail(member.getEmail());
        if (existsByEmail) {
            throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
        }

        boolean existsByNickname = memberRepository.existsByNickname(member.getNickname());
        if (existsByNickname) {
            throw new BusinessException(ErrorCode.DUPLICATED_NICKNAME);
        }
    }
}
