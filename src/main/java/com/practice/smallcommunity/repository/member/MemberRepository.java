package com.practice.smallcommunity.repository.member;

import com.practice.smallcommunity.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByUsername(String username);

    Optional<Member> findByEmail(String email);
}
