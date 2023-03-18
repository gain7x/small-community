package com.practice.smallcommunity.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmailAndWithdrawalIsFalse(String email);

    Optional<Member> findByIdAndWithdrawalIsFalse(Long id);
}
