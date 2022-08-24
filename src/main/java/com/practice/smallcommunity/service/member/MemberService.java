package com.practice.smallcommunity.service.member;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.exception.ValidationError;
import com.practice.smallcommunity.exception.ValidationErrorException;
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
     *
     * 등록되는 회원은 기본적으로 사용자 권한( ROLE_USER )을 보유합니다.
     * @param member 등록할 회원 정보. 단, id 값은 널이어야 합니다.
     * @return 등록된 회원 정보
     * @throws ValidationErrorException
     *          등록하려는 회원 정보가 유효하지 않은 경우( 아이디 중복, 이메일 중복, ... )
     */
    public Member registerMember(Member member) {
        validateRegisterMember(member);

        String encodePassword = passwordEncoder.encode(member.getPassword());
        member.changePassword(encodePassword);
        member.changeMemberRole(MemberRole.ROLE_USER);

        return memberRepository.save(member);
    }

    /**
     * 회원 번호에 해당하는 회원 정보를 반환합니다.
     * @param userId 회원 번호
     * @return 회원 정보
     * @throws IllegalArgumentException
     *          회원 번호에 해당하는 회원이 존재하지 않는 경우
     */
    public Member findByUserId(Long userId) {
        return memberRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id: " + userId));
    }

    private void validateRegisterMember(Member member) {
        boolean existsByEmail = memberRepository.existsByEmail(member.getEmail());
        if (existsByEmail) {
            throw new ValidationErrorException("이미 사용 중인 이메일입니다.",
                ValidationError.of("duplicate", "email"));
        }

        boolean existsByNickname = memberRepository.existsByNickname(member.getNickname());
        if (existsByNickname) {
            throw new ValidationErrorException("이미 사용 중인 별명입니다.",
                ValidationError.of("duplicate", "nickname"));
        }
    }
}
