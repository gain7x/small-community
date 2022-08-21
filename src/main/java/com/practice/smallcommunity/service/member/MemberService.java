package com.practice.smallcommunity.service.member;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member registerMember(Member member) {
        String encodePassword = passwordEncoder.encode(member.getPassword());
        member.changePassword(encodePassword);

        return memberRepository.save(member);
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. username: " + username));
    }
}
